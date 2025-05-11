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

/**
 * REST controller for managing user requirements.
 * Provides endpoints for submitting, retrieving, updating, and deleting
 * requirements,
 * as well as retrieving requirement questions for the wizard.
 */
@RestController
@RequestMapping("/api/requirements")
@RequiredArgsConstructor
public class RequirementController {

    private final RequirementService requirementService;
    private final SessionService sessionService;

    /**
     * Retrieve all requirement questions for the wizard.
     *
     * @return a {@link Flux} containing all requirement questions.
     */
    @GetMapping("/questions")
    public Flux<RequirementQuestion> getAllQuestions() {
        return requirementService.getAllQuestions();
    }

    /**
     * Retrieve requirement questions by category.
     *
     * @param category the category of the questions.
     * @return a {@link Flux} containing the questions for the specified category.
     */
    @GetMapping("/questions/category/{category}")
    public Flux<RequirementQuestion> getQuestionsByCategory(@PathVariable String category) {
        return requirementService.getQuestionsByCategory(category);
    }

    /**
     * Submit user requirements to generate architecture suggestions.
     *
     * @param request  the user requirements submission request.
     * @param exchange the current server exchange to retrieve the session ID.
     * @return a {@link Mono} containing the saved user requirement.
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
     * Retrieve all requirements for the current user/session.
     *
     * @param exchange the current server exchange to retrieve the session ID.
     * @return a {@link Flux} containing all user requirements.
     */
    @GetMapping
    public Flux<UserRequirement> getUserRequirements(ServerWebExchange exchange) {
        return sessionService.getOrCreateSessionId(exchange)
                .flatMapMany(requirementService::getRequirementsByUserId);
    }

    /**
     * Retrieve a specific requirement by its ID.
     *
     * @param id the ID of the requirement.
     * @return a {@link Mono} containing the requirement or a 404 response if not
     *         found.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserRequirement>> getRequirementById(@PathVariable Long id) {
        return requirementService.getRequirementById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Update an existing requirement.
     *
     * @param id      the ID of the requirement to update.
     * @param request the update request containing the modified requirement
     *                details.
     * @return a {@link Mono} containing the updated requirement or a 404 response
     *         if not found.
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
     * Delete a requirement by its ID.
     *
     * @param id the ID of the requirement to delete.
     * @return a {@link Mono} containing a 204 No Content response or a 404 response
     *         if not found.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteRequirement(@PathVariable Long id) {
        return requirementService.deleteRequirement(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
