package com.wisetect.architectureservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wisetect.architectureservice.domain.dto.ArchitectureSuggestionResponse;
import com.wisetect.architectureservice.domain.model.ArchitectureSuggestion;
import com.wisetect.architectureservice.domain.model.UserRequirement;
import com.wisetect.architectureservice.repository.ArchitectureSuggestionRepository;
import com.wisetect.architectureservice.repository.UserRequirementRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class ArchitectureServiceTest {

        @Mock
        private UserRequirementRepository userRequirementRepository;

        @Mock
        private ArchitectureSuggestionRepository architectureSuggestionRepository;

        @Mock
        private LlmService llmService;

        @Spy
        private ObjectMapper objectMapper = new ObjectMapper();

        @InjectMocks
        private ArchitectureService architectureService;

        private UserRequirement testRequirement;
        private Map<String, Object> llmResponse;

        @BeforeEach
        void setUp() {

                objectMapper.findAndRegisterModules();

                // Create a test requirement
                testRequirement = new UserRequirement();
                testRequirement.setId(1L);
                testRequirement.setProjectName("E-commerce Platform");
                testRequirement.setProjectDescription(
                                "Build a scalable e-commerce platform with user authentication");
                testRequirement.setCreatedAt(LocalDateTime.now());
                testRequirement.setUpdatedAt(LocalDateTime.now());

                // Create a mock LLM response
                llmResponse = new HashMap<>();

                // Create diagram data
                Map<String, Object> diagram = new HashMap<>();
                Map<String, Object> components = new HashMap<>();

                Map<String, Object> authService = new HashMap<>();
                authService.put("type", "microservice");
                components.put("authService", authService);

                diagram.put("components", components);
                diagram.put("connections", new HashMap<>());
                llmResponse.put("diagram", diagram);

                // Create analysis data
                Map<String, Object> analysis = new HashMap<>();
                analysis.put("strengths", "Highly scalable, loosely coupled services");
                llmResponse.put("analysis", analysis);
        }

        @Test
        void testGenerateArchitectureSuggestion() {
                // Mock repository to return our test requirement
                when(userRequirementRepository.findById(1L)).thenReturn(Mono.just(testRequirement));

                // Mock LLM service to return our predefined response
                when(llmService.generateArchitectureSuggestion(any())).thenReturn(Mono.just(llmResponse));

                when(architectureSuggestionRepository.save(any())).thenAnswer(invocation -> {
                        ArchitectureSuggestion suggestion = (ArchitectureSuggestion) invocation.getArgument(0);
                        suggestion.setId(1L);

                        // Replace the components array with a key-value map
                        // Updated diagramJson with Connection objects
                        suggestion.setDiagramJson("{"
                                        + "\"type\":\"C4\","
                                        + "\"components\":{"
                                        + "\"authService\":{" // Key-based component
                                        + "\"id\":\"authService\","
                                        + "\"name\":\"Authentication Service\","
                                        + "\"type\":\"microservice\","
                                        + "\"description\":\"Handles user authentication\","
                                        + "\"properties\":{"
                                        + "\"language\":\"Java\","
                                        + "\"framework\":\"Spring Boot\""
                                        + "}"
                                        + "}"
                                        + "},"
                                        + "\"connections\":{"
                                        + "\"conn1\":{" // Connection object (not string)
                                        + "\"id\":\"conn1\","
                                        + "\"source\":\"authService\","
                                        + "\"target\":\"userService\","
                                        + "\"type\":\"HTTP\","
                                        + "\"description\":\"REST API call for user data\","
                                        + "\"properties\":{"
                                        + "\"protocol\":\"HTTPS\","
                                        + "\"timeout\":\"500ms\""
                                        + "}"
                                        + "}"
                                        + "}"
                                        + "}");

                        // Analysis JSON with no empty values
                        // Updated analysisJson with proper nested structure
                        suggestion.setAnalysisJson("{"
                                        + "\"summary\":\"Scalable but needs redundancy improvements\","
                                        + "\"strengths\":{" // Map (key-value pairs)
                                        + "\"strength1\":{" // Unique key
                                        + "\"title\":\"Modular Design\","
                                        + "\"description\":\"Independent microservices enable easy scaling\","
                                        + "\"impact\":\"High\""
                                        + "}"
                                        + "},"
                                        + "\"weaknesses\":{"
                                        + "\"weakness1\":{"
                                        + "\"title\":\"Single Point of Failure\","
                                        + "\"description\":\"Auth service has no fallback\","
                                        + "\"impact\":\"Critical\","
                                        + "\"mitigationStrategy\":\"Add cluster deployment\""
                                        + "}"
                                        + "},"
                                        + "\"recommendations\":{"
                                        + "\"recommendation1\":{"
                                        + "\"title\":\"Implement Redis Cache\","
                                        + "\"description\":\"Cache auth tokens to reduce DB load\","
                                        + "\"priority\":\"High\","
                                        + "\"effort\":\"Medium\","
                                        + "\"impact\":\"20% performance gain\""
                                        + "}"
                                        + "},"
                                        + "\"metrics\":{"
                                        + "\"throughput\":\"1500 req/sec\","
                                        + "\"errorRate\":\"0.5%\""
                                        + "}"
                                        + "}");

                        suggestion.setVersion(1);
                        suggestion.setRequirementId(1L);
                        return Mono.just(suggestion);
                });

                when(architectureSuggestionRepository.findByRequirementIdAndIsActiveTrue(any()))
                                .thenReturn(Mono.empty());

                when(architectureSuggestionRepository.findByRequirementIdOrderByVersionDesc(any()))
                                .thenReturn(Flux.empty());

                // Test the service method
                Mono<ArchitectureSuggestionResponse> result = architectureService.generateArchitectureSuggestion(1L);

                StepVerifier.create(result)
                                .expectNextMatches(response -> response.getId() == 1L &&
                                                response.getRequirementId() == 1L &&
                                                response.getVersion() == 1 &&

                                                // Diagram Assertions
                                                response.getDiagram() != null &&
                                                response.getDiagram().getType().equals("C4") &&
                                                response.getDiagram().getComponents().size() == 1 &&
                                                response.getDiagram().getComponents().get("authService").getId()
                                                                .equals("authService")
                                                &&
                                                response.getDiagram().getComponents().get("authService").getType()
                                                                .equals("microservice")
                                                &&
                                                response.getDiagram().getConnections().size() == 1 &&
                                                response.getDiagram().getConnections().get("conn1").getSource()
                                                                .equals("authService")
                                                && // Check Connection object
                                                response.getDiagram().getConnections().get("conn1").getDescription()
                                                                .equals("REST API call for user data")

                                                &&

                                                // Analysis Assertions
                                                response.getAnalysis() != null &&
                                                response.getAnalysis().getSummary()
                                                                .equals("Scalable but needs redundancy improvements")
                                                &&
                                                response.getAnalysis().getStrengths().size() == 1 &&
                                                response.getAnalysis().getStrengths().get("strength1").getTitle()
                                                                .equals("Modular Design")
                                                && // Key-based access
                                                response.getAnalysis().getWeaknesses().size() == 1 &&
                                                response.getAnalysis().getWeaknesses().get("weakness1").getTitle()
                                                                .equals("Single Point of Failure")
                                                &&
                                                response.getAnalysis().getRecommendations().size() == 1 &&
                                                response.getAnalysis().getRecommendations().get("recommendation1")
                                                                .getTitle().equals("Implement Redis Cache")
                                                &&
                                                response.getAnalysis().getMetrics().size() == 2 &&
                                                response.getAnalysis().getMetrics().get("throughput")
                                                                .equals("1500 req/sec"))
                                .verifyComplete();
        }

}
