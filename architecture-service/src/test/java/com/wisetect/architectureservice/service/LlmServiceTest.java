package com.wisetect.architectureservice.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.wisetect.architectureservice.domain.model.UserRequirement;
import com.wisetect.architectureservice.exception.LlmProcessingException;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class LlmServiceTest {

    private static final String TEST_PROJECT_NAME = "Test Project";
    private static final String TEST_DESCRIPTION = "Test Description";
    private static final String MOCK_RESPONSE_TEXT = "{\"diagram\": {\"components\": []}, \"analysis\": {\"strengths\": []}}";

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private LlmService llmService;

    private UserRequirement testRequirement;

    @BeforeEach
    void setUp() {
        initializeTestRequirement();
    }

    @Test
    void testGenerateArchitectureSuggestion_BasicFlow() {
        // Prepare test data
        Map<String, Object> requirement = createTestRequirementMap();

        // Prepare mock response
        Map<String, Object> mockResponse = createMockLlmResponse();

        // Set up WebClient mock chain
        setupWebClientMockChain(mockResponse);

        // Execute test and verify results
        StepVerifier.create(llmService.generateArchitectureSuggestion(requirement))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertTrue(result.containsKey("diagram"));
                    assertTrue(result.containsKey("analysis"));
                })
                .verifyComplete();

        verify(webClient).post();
    }

    private void initializeTestRequirement() {
        testRequirement = new UserRequirement();
        testRequirement.setId(1L);
        testRequirement.setProjectName("E-commerce Platform");
        testRequirement.setProjectDescription(
                "Build a scalable e-commerce platform with user authentication");
        testRequirement.setCreatedAt(LocalDateTime.now());
        testRequirement.setUpdatedAt(LocalDateTime.now());
    }

    private Map<String, Object> createTestRequirementMap() {
        Map<String, Object> requirement = new HashMap<>();
        requirement.put("projectName", TEST_PROJECT_NAME);
        requirement.put("description", TEST_DESCRIPTION);
        return requirement;
    }

    private Map<String, Object> createMockLlmResponse() {
        Map<String, Object> mockResponse = new HashMap<>();
        List<Map<String, Object>> candidates = new ArrayList<>();
        Map<String, Object> candidate = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        List<Map<String, Object>> parts = new ArrayList<>();
        Map<String, Object> part = new HashMap<>();

        part.put("text", MOCK_RESPONSE_TEXT);
        parts.add(part);
        content.put("parts", parts);
        candidate.put("content", content);
        candidates.add(candidate);
        mockResponse.put("candidates", candidates);

        return mockResponse;
    }

    private void setupWebClientMockChain(Map<String, Object> mockResponse) {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.just(mockResponse));
    }

    @Test
    void testProcessLlmResponse_InvalidJsonContent() {
        // Prepare mock response with invalid JSON content
        Map<String, Object> mockResponse = new HashMap<>();
        List<Map<String, Object>> candidates = new ArrayList<>();
        Map<String, Object> candidate = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        List<Map<String, Object>> parts = new ArrayList<>();
        Map<String, Object> part = new HashMap<>();

        part.put("text", "This is not valid JSON");
        parts.add(part);
        content.put("parts", parts);
        candidate.put("content", content);
        candidates.add(candidate);
        mockResponse.put("candidates", candidates);

        // Set up WebClient mock chain
        setupWebClientMockChain(mockResponse);

        // Execute test and verify error handling
        Map<String, Object> requirement = createTestRequirementMap();
        StepVerifier.create(llmService.generateArchitectureSuggestion(requirement))
                .expectErrorMatches(throwable -> throwable instanceof LlmProcessingException &&
                        throwable.getMessage().contains("Invalid JSON format in LLM response"))
                .verify();
    }

    @Test
    void testGenerateArchitectureSuggestion_ApiError() {
        // Prepare test data
        Map<String, Object> requirement = createTestRequirementMap();

        // Set up WebClient mock to return an error
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.error(new RuntimeException("API Error")));

        // Execute test and verify error handling
        StepVerifier.create(llmService.generateArchitectureSuggestion(requirement))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("API Error"))
                .verify();
    }

    @Test
    void testGenerateArchitectureSuggestion_ComplexRequirement() {
        // Prepare a more complex test requirement
        Map<String, Object> requirement = new HashMap<>();
        requirement.put("projectName", "Enterprise CRM System");
        requirement.put("description", "Build a scalable CRM system with multi-tenant support");
        requirement.put("constraints", "Must support at least 10,000 concurrent users");
        requirement.put("technologies", List.of("Java", "Spring Boot", "React", "PostgreSQL"));
        requirement.put("securityRequirements", "SOC2 compliance required");

        // Prepare mock response
        Map<String, Object> mockResponse = createMockLlmResponse();

        // Set up WebClient mock chain
        setupWebClientMockChain(mockResponse);

        // Execute test and verify results
        StepVerifier.create(llmService.generateArchitectureSuggestion(requirement))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertTrue(result.containsKey("diagram"));
                    assertTrue(result.containsKey("analysis"));
                })
                .verifyComplete();

        verify(webClient).post();
    }

    @Test
    void testProcessLlmResponse_MissingDiagramOrAnalysis() {
        // Prepare mock response with incomplete JSON structure
        Map<String, Object> mockResponse = new HashMap<>();
        List<Map<String, Object>> candidates = new ArrayList<>();
        Map<String, Object> candidate = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        List<Map<String, Object>> parts = new ArrayList<>();
        Map<String, Object> part = new HashMap<>();

        // JSON missing the analysis field
        part.put("text", "{\"diagram\": {\"components\": []}}");
        parts.add(part);
        content.put("parts", parts);
        candidate.put("content", content);
        candidates.add(candidate);
        mockResponse.put("candidates", candidates);

        // Set up WebClient mock chain
        setupWebClientMockChain(mockResponse);

        // Execute test and verify results
        Map<String, Object> requirement = createTestRequirementMap();
        StepVerifier.create(llmService.generateArchitectureSuggestion(requirement))
                .expectErrorMatches(throwable -> throwable instanceof LlmProcessingException &&
                        throwable.getMessage().contains("Response missing required fields (diagram or analysis)"))
                .verify();
    }
}
