package com.wisetect.architectureservice.repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.wisetect.architectureservice.domain.model.UserRequirement;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test") // Add test profile for database configuration
public class UserRequirementRepositoryTest {

        @Autowired
        private UserRequirementRepository userRequirementRepository;

        @Test
        void testSaveAndFindById() {
                // Create a test requirement
                UserRequirement requirement = new UserRequirement();
                // Don't set ID as it should be auto-generated
                requirement.setProjectName("Test Project");
                requirement.setProjectDescription("Test Description");
                requirement.setCreatedAt(LocalDateTime.now());
                requirement.setUpdatedAt(LocalDateTime.now());
                requirement.setUserId("test-user-id");

                // Initialize the answers map with some test data
                Map<String, Object> answers = new HashMap<>();
                answers.put("systemType", "web application");
                answers.put("userCount", 1000);
                answers.put("dataVolume", "medium");
                answers.put("securityLevel", "high");
                answers.put("scalabilityNeeds", "moderate");
                requirement.setAnswers(answers);

                // Save and then find by ID
                Mono<UserRequirement> savedAndFound = userRequirementRepository.save(requirement)
                                .flatMap(saved -> userRequirementRepository.findById(saved.getId()));

                // Verify
                StepVerifier.create(savedAndFound)
                                .expectNextMatches(found -> found.getProjectName().equals("Test Project") &&
                                                found.getProjectDescription().equals("Test Description") &&
                                                found.getAnswers().containsKey("systemType") &&
                                                "web application".equals(found.getAnswers().get("systemType")) &&
                                                found.getAnswers().containsKey("userCount") &&
                                                Integer.valueOf(1000).equals(found.getAnswers().get("userCount")))
                                .verifyComplete();
        }
}
