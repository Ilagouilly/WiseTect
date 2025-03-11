package com.wisetect.architectureservice.repository;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.ActiveProfiles;

import com.wisetect.architectureservice.domain.model.ArchitectureSuggestion;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataR2dbcTest
@ActiveProfiles("test")
public class ArchitectureSuggestionRepositoryTest {

        @Autowired
        private ArchitectureSuggestionRepository architectureSuggestionRepository;

        @Test
        void testSaveAndFindById() {
                // Create a test suggestion
                ArchitectureSuggestion suggestion = ArchitectureSuggestion.builder()
                                .requirementId(1L)
                                .diagramJson("{\"test\":\"diagram\"}")
                                .analysisJson("{\"test\":\"analysis\"}")
                                .rawLlmResponse("{\"test\":\"response\"}")
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .version(1)
                                .isActive(true)
                                .build();

                // Save and then find by ID
                Mono<ArchitectureSuggestion> savedAndFound = architectureSuggestionRepository.save(suggestion)
                                .flatMap(saved -> architectureSuggestionRepository.findById(saved.getId()));

                // Verify
                StepVerifier.create(savedAndFound)
                                .expectNextMatches(found -> found.getRequirementId().equals(1L) &&
                                                found.getDiagramJson().equals("{\"test\":\"diagram\"}") &&
                                                found.getAnalysisJson().equals("{\"test\":\"analysis\"}"))
                                .verifyComplete();
        }
}
