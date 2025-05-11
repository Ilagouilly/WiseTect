package com.wisetect.architectureservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wisetect.architectureservice.domain.dto.ArchitectureSuggestionResponse;
import com.wisetect.architectureservice.domain.dto.ArchitectureUpdateRequest;
import com.wisetect.architectureservice.service.ArchitectureService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * REST controller for managing architecture suggestions.
 * Provides endpoints for generating, retrieving, updating, and exporting
 * architecture suggestions.
 */
@RestController
@RequestMapping("/api/architecture")
@RequiredArgsConstructor
public class ArchitectureController {

    private final ArchitectureService architectureService;

    /**
     * Generate an architecture suggestion based on the given requirement ID.
     *
     * @param requirementId the ID of the requirement.
     * @return a {@link Mono} containing the generated architecture suggestion or a
     *         404 response if not found.
     */
    @PostMapping("/generate/{requirementId}")
    public Mono<ResponseEntity<ArchitectureSuggestionResponse>> generateArchitecture(@PathVariable Long requirementId) {
        return architectureService.generateArchitectureSuggestion(requirementId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Retrieve the latest architecture suggestion for a given requirement ID.
     *
     * @param requirementId the ID of the requirement.
     * @return a {@link Mono} containing the latest architecture suggestion or a 404
     *         response if not found.
     */
    @GetMapping("/latest/{requirementId}")
    public Mono<ResponseEntity<ArchitectureSuggestionResponse>> getLatestArchitecture(
            @PathVariable Long requirementId) {
        return architectureService.getLatestArchitectureSuggestion(requirementId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Retrieve all versions of architecture suggestions for a given requirement ID.
     *
     * @param requirementId the ID of the requirement.
     * @return a {@link Flux} containing all versions of architecture suggestions.
     */
    @GetMapping("/versions/{requirementId}")
    public Flux<ArchitectureSuggestionResponse> getArchitectureVersions(@PathVariable Long requirementId) {
        return architectureService.getArchitectureVersions(requirementId);
    }

    /**
     * Retrieve a specific architecture suggestion by its ID.
     *
     * @param id the ID of the architecture suggestion.
     * @return a {@link Mono} containing the architecture suggestion or a 404
     *         response if not found.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ArchitectureSuggestionResponse>> getArchitectureById(@PathVariable Long id) {
        return architectureService.getArchitectureSuggestionById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Update an architecture suggestion based on user modifications.
     *
     * @param id      the ID of the architecture suggestion to update.
     * @param request the update request containing the modified architecture
     *                details.
     * @return a {@link Mono} containing the updated architecture suggestion.
     */
    @PutMapping("/update/{id}")
    public Mono<ResponseEntity<ArchitectureSuggestionResponse>> updateArchitecture(@PathVariable Long id,
            @Valid @RequestBody ArchitectureUpdateRequest request) {
        return architectureService.updateArchitectureSuggestion(id, request)
                .map(ResponseEntity::ok);
    }

    /**
     * Export an architecture suggestion as a JSON string.
     *
     * @param id the ID of the architecture suggestion to export.
     * @return a {@link Mono} containing the exported JSON string or a 404 response
     *         if not found.
     */
    @GetMapping("/export/{id}")
    public Mono<ResponseEntity<String>> exportArchitecture(@PathVariable Long id) {
        return architectureService.exportArchitectureAsJson(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
