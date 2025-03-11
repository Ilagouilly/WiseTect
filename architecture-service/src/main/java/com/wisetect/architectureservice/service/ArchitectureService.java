package com.wisetect.architectureservice.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wisetect.architectureservice.domain.dto.ArchitectureSuggestionResponse;
import com.wisetect.architectureservice.domain.dto.ArchitectureUpdateRequest;
import com.wisetect.architectureservice.domain.model.ArchitectureAnalysis;
import com.wisetect.architectureservice.domain.model.ArchitectureDiagram;
import com.wisetect.architectureservice.domain.model.ArchitectureSuggestion;
import com.wisetect.architectureservice.domain.model.UserRequirement;
import com.wisetect.architectureservice.repository.ArchitectureSuggestionRepository;
import com.wisetect.architectureservice.repository.UserRequirementRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArchitectureService {

    private final ArchitectureSuggestionRepository architectureSuggestionRepository;
    private final UserRequirementRepository userRequirementRepository;
    private final LlmService llmService;
    private final ObjectMapper objectMapper;

    public Mono<ArchitectureSuggestionResponse> generateArchitectureSuggestion(Long requirementId) {
        log.info("Generating architecture suggestion for requirement: {}", requirementId);

        return userRequirementRepository.findById(requirementId)
                .switchIfEmpty(
                        Mono.error(new IllegalArgumentException("Requirement not found with id: " + requirementId)))
                .flatMap(requirement -> {
                    Map<String, Object> requirementData = convertRequirementToMap(requirement);
                    return llmService.generateArchitectureSuggestion(requirementData)
                            .flatMap(llmResponse -> saveArchitectureSuggestion(requirement, llmResponse));
                })
                .map(this::mapToResponse);
    }

    public Mono<ArchitectureSuggestionResponse> getLatestArchitectureSuggestion(Long requirementId) {
        log.info("Fetching latest architecture suggestion for requirement: {}", requirementId);

        return architectureSuggestionRepository.findByRequirementIdAndIsActiveTrue(requirementId)
                .switchIfEmpty(Mono.empty())
                .map(this::mapToResponse);
    }

    public Flux<ArchitectureSuggestionResponse> getArchitectureVersions(Long requirementId) {
        log.info("Fetching all architecture versions for requirement: {}", requirementId);

        return architectureSuggestionRepository.findByRequirementIdOrderByVersionDesc(requirementId)
                .map(this::mapToResponse);
    }

    public Mono<ArchitectureSuggestionResponse> getArchitectureSuggestionById(Long id) {
        log.info("Fetching architecture suggestion with id: {}", id);

        return architectureSuggestionRepository.findById(id)
                .map(this::mapToResponse);
    }

    public Mono<ArchitectureSuggestionResponse> updateArchitectureSuggestion(Long id,
            ArchitectureUpdateRequest request) {
        log.info("Updating architecture suggestion with id: {}", id);

        return architectureSuggestionRepository.findById(id)
                .switchIfEmpty(
                        Mono.error(new IllegalArgumentException("Architecture suggestion not found with id: " + id)))
                .flatMap(suggestion -> {
                    suggestion.setUpdatedAt(LocalDateTime.now());

                    // Update the diagram and analysis JSON based on the request
                    if (request.getDiagramJson() != null) {
                        suggestion.setDiagramJson(request.getDiagramJson());
                    }

                    if (request.getAnalysisJson() != null) {
                        suggestion.setAnalysisJson(request.getAnalysisJson());
                    }

                    return architectureSuggestionRepository.save(suggestion);
                })
                .map(this::mapToResponse);
    }

    public Mono<String> exportArchitectureAsJson(Long id) {
        log.info("Exporting architecture with id: {} as JSON", id);

        return architectureSuggestionRepository.findById(id)
                .map(suggestion -> {
                    try {
                        return objectMapper.writeValueAsString(mapToResponse(suggestion));
                    } catch (JsonProcessingException e) {
                        log.error("Error serializing architecture to JSON", e);
                        throw new RuntimeException("Failed to export architecture as JSON", e);
                    }
                });
    }

    private Map<String, Object> convertRequirementToMap(UserRequirement requirement) {
        Map<String, Object> requirementMap = new HashMap<>();
        requirementMap.put("id", requirement.getId());
        requirementMap.put("userId", requirement.getUserId());
        requirementMap.put("projectName", requirement.getProjectName());
        requirementMap.put("projectDescription", requirement.getProjectDescription());
        requirementMap.put("answers", requirement.getAnswers());
        requirementMap.put("createdAt", requirement.getCreatedAt());
        requirementMap.put("updatedAt", requirement.getUpdatedAt());
        return requirementMap;
    }

    private Mono<ArchitectureSuggestion> saveArchitectureSuggestion(UserRequirement requirement,
            Map<String, Object> llmResponse) {
        // First, deactivate any existing active suggestions for this requirement
        return architectureSuggestionRepository.findByRequirementIdAndIsActiveTrue(requirement.getId())
                .flatMap(existingSuggestion -> {
                    existingSuggestion.setIsActive(false);
                    return architectureSuggestionRepository.save(existingSuggestion);
                })
                .switchIfEmpty(Mono.empty())
                .then(Mono.defer(() -> {
                    // Get the latest version number
                    return architectureSuggestionRepository.findByRequirementIdOrderByVersionDesc(requirement.getId())
                            .next()
                            .map(latest -> latest.getVersion() + 1)
                            .defaultIfEmpty(1);
                }))
                .flatMap(newVersion -> {
                    ArchitectureSuggestion suggestion = new ArchitectureSuggestion();
                    suggestion.setRequirementId(requirement.getId());
                    suggestion.setCreatedAt(LocalDateTime.now());
                    suggestion.setUpdatedAt(LocalDateTime.now());
                    suggestion.setIsActive(true);
                    suggestion.setVersion(newVersion);

                    // Convert LLM response to appropriate JSON strings
                    try {
                        if (llmResponse.containsKey("diagram")) {
                            suggestion.setDiagramJson(objectMapper.writeValueAsString(llmResponse.get("diagram")));
                        }

                        if (llmResponse.containsKey("analysis")) {
                            suggestion.setAnalysisJson(objectMapper.writeValueAsString(llmResponse.get("analysis")));
                        }

                        // Store the raw LLM response for reference
                        suggestion.setRawLlmResponse(objectMapper.writeValueAsString(llmResponse));
                    } catch (JsonProcessingException e) {
                        log.error("Error processing LLM response", e);
                        return Mono.error(new RuntimeException("Failed to process LLM response", e));
                    }

                    return architectureSuggestionRepository.save(suggestion);
                });
    }

    private ArchitectureSuggestionResponse mapToResponse(ArchitectureSuggestion suggestion) {
        ArchitectureDiagram diagram = null;
        ArchitectureAnalysis analysis = null;

        try {
            if (suggestion.getDiagramJson() != null) {
                JsonNode diagramNode = objectMapper.readTree(suggestion.getDiagramJson());
                if (diagramNode.isTextual()) {
                    // Handle case where diagram is a stringified JSON object
                    diagram = objectMapper.readValue(diagramNode.textValue(), ArchitectureDiagram.class);
                } else {
                    diagram = objectMapper.treeToValue(diagramNode, ArchitectureDiagram.class);
                }
            }

            if (suggestion.getAnalysisJson() != null) {
                System.out.println(" getAnalysisJson: " + suggestion.getAnalysisJson());
                JsonNode analysisNode = objectMapper.readTree(suggestion.getAnalysisJson());
                if (analysisNode.isTextual()) {
                    analysis = objectMapper.readValue(analysisNode.textValue(), ArchitectureAnalysis.class);
                } else {
                    analysis = objectMapper.treeToValue(analysisNode, ArchitectureAnalysis.class);
                }
            }
        } catch (JsonProcessingException e) {
            log.error("Error deserializing architecture data", e);
        }

        return ArchitectureSuggestionResponse.builder()
                .id(suggestion.getId())
                .requirementId(suggestion.getRequirementId())
                .diagram(diagram)
                .analysis(analysis)
                .version(suggestion.getVersion())
                .build();
    }
}
