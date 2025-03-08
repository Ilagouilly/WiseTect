package com.wisetect.architectureservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.wisetect.architectureservice.domain.dto.RequirementSubmissionRequest;
import com.wisetect.architectureservice.domain.model.RequirementQuestion;
import com.wisetect.architectureservice.domain.model.UserRequirement;
import com.wisetect.architectureservice.service.RequirementService;
import com.wisetect.architectureservice.service.SessionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/requirements")
@RequiredArgsConstructor
public class RequirementController {

    private final RequirementService requirementService;
    private final SessionService sessionService;

    /**
     * Get all requirement questions for the wizard
     */
    @GetMapping("/questions")
    public Flux<RequirementQuestion> getAllQuestions() {
        return requirementService.getAllQuestions();
    }

    /**
     * Get questions by category
     */
    @GetMapping("/questions/category/{category}")
    public Flux<RequirementQuestion> getQuestionsByCategory(@PathVariable String category) {
        return requirementService.getQuestionsByCategory(category);
    }

    /**
     * Submit user requirements to generate architecture
     */
    @PostMapping
    public Mono<ResponseEntity<UserRequirement>> submitRequirements(
            @Valid @RequestBody RequirementSubmissionRequest request,
            ServerWebExchange exchange) {

        return sessionService.getOrCreateSessionId(exchange)
                .flatMap(sessionId -> requirementService.saveRequirements(sessionId, request))
                .map(ResponseEntity::ok);
    }

    /**
     * Get all requirements for the current user/session
     */
    @GetMapping
    public Flux<UserRequirement> getUserRequirements(ServerWebExchange exchange) {
        return sessionService.getOrCreateSessionId(exchange)
                .flatMapMany(requirementService::getRequirementsByUserId);
    }

    /**
     * Get a specific requirement by ID
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserRequirement>> getRequirementById(@PathVariable Long id) {
        return requirementService.getRequirementById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Update an existing requirement
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<UserRequirement>> updateRequirement(
            @PathVariable Long id,
            @Valid @RequestBody RequirementSubmissionRequest request) {

        return requirementService.updateRequirement(id, request)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Delete a requirement
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteRequirement(@PathVariable Long id) {
        return requirementService.deleteRequirement(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
