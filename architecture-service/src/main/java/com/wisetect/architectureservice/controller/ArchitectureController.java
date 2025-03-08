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

@RestController
@RequestMapping("/api/architecture")
@RequiredArgsConstructor
public class ArchitectureController {

    private final ArchitectureService architectureService;

    /**
     * Generate architecture suggestion based on requirements
     */
    @PostMapping("/generate/{requirementId}")
    public Mono<ResponseEntity<ArchitectureSuggestionResponse>> generateArchitecture(@PathVariable Long requirementId) {
        return architectureService.generateArchitectureSuggestion(requirementId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Get the latest architecture suggestion for a requirement
     */
    @GetMapping("/latest/{requirementId}")
    public Mono<ResponseEntity<ArchitectureSuggestionResponse>> getLatestArchitecture(
            @PathVariable Long requirementId) {
        return architectureService.getLatestArchitectureSuggestion(requirementId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Get all versions of architecture suggestions for a requirement
     */
    @GetMapping("/versions/{requirementId}")
    public Flux<ArchitectureSuggestionResponse> getArchitectureVersions(@PathVariable Long requirementId) {
        return architectureService.getArchitectureVersions(requirementId);
    }

    /**
     * Get a specific architecture suggestion by ID
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ArchitectureSuggestionResponse>> getArchitectureById(@PathVariable Long id) {
        return architectureService.getArchitectureSuggestionById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Update architecture based on user modifications
     */
    @PutMapping("/update")
    public Mono<ResponseEntity<ArchitectureSuggestionResponse>> updateArchitecture(@PathVariable Long id,
            @Valid @RequestBody ArchitectureUpdateRequest request) {

        return architectureService.updateArchitectureSuggestion(id, request)
                .map(ResponseEntity::ok);
    }

    /**
     * Export architecture as JSON
     */
    @GetMapping("/export/{id}")
    public Mono<ResponseEntity<String>> exportArchitecture(@PathVariable Long id) {
        return architectureService.exportArchitectureAsJson(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
