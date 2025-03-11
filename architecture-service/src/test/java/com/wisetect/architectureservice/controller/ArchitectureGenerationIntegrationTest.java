package com.wisetect.architectureservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wisetect.architectureservice.domain.model.ArchitectureSuggestion;
import com.wisetect.architectureservice.domain.model.UserRequirement;
import com.wisetect.architectureservice.repository.ArchitectureSuggestionRepository;
import com.wisetect.architectureservice.repository.UserRequirementRepository;
import com.wisetect.architectureservice.service.LlmService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ArchitectureGenerationIntegrationTest {

        @Autowired
        private WebTestClient webTestClient;

        @MockitoBean
        private UserRequirementRepository userRequirementRepository;

        @MockitoBean
        private ArchitectureSuggestionRepository architectureSuggestionRepository;

        @MockitoBean
        private LlmService llmService;

        private UserRequirement testRequirement;
        private Map<String, Object> llmResponse;
        private final ObjectMapper objectMapper = new ObjectMapper();

        @BeforeEach
        void setUp() {
                // Create test requirement
                testRequirement = new UserRequirement();
                testRequirement.setId(1L);
                testRequirement.setProjectName("E-commerce Platform");
                testRequirement.setProjectDescription(
                                "Build a scalable e-commerce platform with user authentication");
                testRequirement.setCreatedAt(LocalDateTime.now());
                testRequirement.setUpdatedAt(LocalDateTime.now());

                // Build LLM response with proper structures
                llmResponse = new HashMap<>();

                // 1. Diagram Data (match ArchitectureDiagram class structure)
                Map<String, Object> diagram = new HashMap<>();
                diagram.put("type", "C4");

                // Components
                Map<String, Object> components = new HashMap<>();
                Map<String, Object> authService = new HashMap<>();
                authService.put("id", "authService");
                authService.put("type", "microservice");
                authService.put("description", "Handles user authentication");
                components.put("authService", authService);
                diagram.put("components", components);

                // Connections (as objects, not strings)
                Map<String, Object> connections = new HashMap<>();
                Map<String, Object> conn1 = new HashMap<>();
                conn1.put("id", "conn1");
                conn1.put("source", "authService");
                conn1.put("target", "productService");
                conn1.put("type", "REST API");
                connections.put("conn1", conn1);
                diagram.put("connections", connections);

                llmResponse.put("diagram", diagram);

                // 2. Analysis Data (match ArchitectureAnalysis class structure)
                Map<String, Object> analysis = new HashMap<>();

                // Strengths
                Map<String, Object> strengths = new HashMap<>();
                Map<String, Object> strength1 = new HashMap<>();
                strength1.put("title", "Modular Design");
                strength1.put("description", "Independent microservices enable easy scaling");
                strength1.put("impact", "High");
                strengths.put("strength1", strength1);
                analysis.put("strengths", strengths);

                // Weaknesses
                Map<String, Object> weaknesses = new HashMap<>();
                Map<String, Object> weakness1 = new HashMap<>();
                weakness1.put("title", "Single Point of Failure");
                weakness1.put("description", "Auth service has no fallback");
                weakness1.put("mitigationStrategy", "Add cluster deployment");
                weaknesses.put("weakness1", weakness1);
                analysis.put("weaknesses", weaknesses);

                // Recommendations
                Map<String, Object> recommendations = new HashMap<>();
                Map<String, Object> recommendation1 = new HashMap<>();
                recommendation1.put("title", "Implement Redis Cache");
                recommendation1.put("description", "Cache auth tokens to reduce DB load");
                recommendation1.put("priority", "High");
                recommendations.put("recommendation1", recommendation1);
                analysis.put("recommendations", recommendations);

                // Metrics
                analysis.put("metrics", Map.of(
                                "throughput", "1500 req/sec",
                                "errorRate", "0.5%"));

                llmResponse.put("analysis", analysis);
        }

        @Test
        void testGenerateArchitectureSuggestion() throws JsonProcessingException {
                // Mock repository responses
                when(userRequirementRepository.findById(1L)).thenReturn(Mono.just(testRequirement));
                when(llmService.generateArchitectureSuggestion(any())).thenReturn(Mono.just(llmResponse));
                when(architectureSuggestionRepository.findByRequirementIdAndIsActiveTrue(1L)).thenReturn(Mono.empty());
                when(architectureSuggestionRepository.findByRequirementIdOrderByVersionDesc(1L))
                                .thenReturn(Flux.empty());

                // Mock save with proper JSON serialization
                when(architectureSuggestionRepository.save(any())).thenAnswer(invocation -> {
                        ArchitectureSuggestion suggestion = invocation.getArgument(0);
                        suggestion.setId(1L);

                        // Serialize diagram/analysis to match service expectations
                        suggestion.setDiagramJson(objectMapper.writeValueAsString(llmResponse.get("diagram")));
                        suggestion.setAnalysisJson(objectMapper.writeValueAsString(llmResponse.get("analysis")));

                        return Mono.just(suggestion);
                });

                // Test endpoint
                webTestClient.post()
                                .uri("/api/architecture/generate/1")
                                .accept(MediaType.APPLICATION_JSON)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.diagram").exists() // Verify diagram exists
                                .jsonPath("$.diagram.components.authService.type").isEqualTo("microservice")
                                .jsonPath("$.diagram.connections.conn1.type").isEqualTo("REST API")
                                .jsonPath("$.analysis.strengths.strength1.title").isEqualTo("Modular Design")
                                .jsonPath("$.analysis.weaknesses.weakness1.title").isEqualTo("Single Point of Failure")
                                .jsonPath("$.analysis.recommendations.recommendation1.title")
                                .isEqualTo("Implement Redis Cache")
                                .jsonPath("$.analysis.metrics.throughput").isEqualTo("1500 req/sec")
                                .jsonPath("$.analysis.metrics.errorRate").isEqualTo("0.5%");
        }
}
