package com.wisetect.architectureservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.wisetect.architectureservice.domain.dto.ArchitectureSuggestionResponse;
import com.wisetect.architectureservice.service.ArchitectureService;

@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test") // Add test profile for database configuration
public class ArchitectureControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ArchitectureService architectureService;

    private ArchitectureSuggestionResponse testSuggestion;

    @BeforeEach
    void setUp() {
        testSuggestion = ArchitectureSuggestionResponse.builder()
                .id(1L)
                .requirementId(1L)
                .build();
    }

    @Test
    void testGenerateArchitectureSuggestion() {
        /*
         * // Mock service to return our test suggestion
         * when(architectureService.generateArchitectureSuggestion(eq(1L))).thenReturn(
         * Mono.just(testSuggestion));
         *
         * // Test the endpoint
         * webTestClient.post()
         * .uri("/api/architecture/generate/1")
         * .accept(MediaType.APPLICATION_JSON)
         * .exchange()
         * .expectStatus().isOk()
         * .expectBody()
         * .jsonPath("$.id").isEqualTo(1)
         * .jsonPath("$.requirementId").isEqualTo(1);
         */
    }
}
