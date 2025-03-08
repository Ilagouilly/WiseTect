package com.wisetect.architectureservice.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.wisetect.architectureservice.domain.model.ArchitectureSuggestion;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ArchitectureSuggestionRepository extends R2dbcRepository<ArchitectureSuggestion, Long> {
    Flux<ArchitectureSuggestion> findByRequirementIdOrderByVersionDesc(Long requirementId);

    Mono<ArchitectureSuggestion> findByRequirementIdAndIsActiveTrue(Long requirementId);
}
