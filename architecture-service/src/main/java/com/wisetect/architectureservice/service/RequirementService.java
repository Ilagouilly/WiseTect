package com.wisetect.architectureservice.service;

import org.springframework.stereotype.Service;

import com.wisetect.architectureservice.domain.dto.RequirementSubmissionRequest;
import com.wisetect.architectureservice.domain.model.RequirementQuestion;
import com.wisetect.architectureservice.domain.model.UserRequirement;
import com.wisetect.architectureservice.repository.RequirementQuestionRepository;
import com.wisetect.architectureservice.repository.UserRequirementRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequirementService {

    private final RequirementQuestionRepository questionRepository;
    private final UserRequirementRepository requirementRepository;

    /**
     * Get all requirement questions for the wizard
     */
    public Flux<RequirementQuestion> getAllQuestions() {
        return questionRepository.findAll();
    }

    /**
     * Get questions by category
     */
    public Flux<RequirementQuestion> getQuestionsByCategory(String category) {
        return questionRepository.findAll()
                .filter(question -> category.equals(question.getCategory()));
    }

    /**
     * Save user requirements
     */
    public Mono<UserRequirement> saveRequirements(String sessionId, RequirementSubmissionRequest request) {
        UserRequirement requirement = new UserRequirement();
        requirement.setUserId(sessionId);
        requirement.setProjectName(request.getProjectName());
        requirement.setProjectDescription(request.getProjectDescription());
        requirement.setAnswers(request.getAnswers());
        requirement.setCreatedAt(
                java.time.LocalDateTime.ofInstant(java.time.Instant.now(), java.time.ZoneId.systemDefault()));
        return requirementRepository.save(requirement);
    }

    /**
     * Get requirements by user ID
     */
    public Flux<UserRequirement> getRequirementsByUserId(String userId) {
        return requirementRepository.findAll()
                .filter(req -> userId.equals(req.getUserId()));
    }

    /**
     * Get requirement by ID
     */
    public Mono<UserRequirement> getRequirementById(Long id) {
        return requirementRepository.findById(id);
    }

    /**
     * Update an existing requirement
     */
    public Mono<UserRequirement> updateRequirement(Long id, RequirementSubmissionRequest request) {
        return requirementRepository.findById(id)
                .flatMap(existing -> {
                    existing.setAnswers(request.getAnswers());
                    existing.setUpdatedAt(java.time.LocalDateTime.ofInstant(java.time.Instant.now(),
                            java.time.ZoneId.systemDefault()));
                    return requirementRepository.save(existing);
                });
    }

    /**
     * Delete a requirement
     */
    public Mono<Void> deleteRequirement(Long id) {
        return requirementRepository.deleteById(id);
    }
}
