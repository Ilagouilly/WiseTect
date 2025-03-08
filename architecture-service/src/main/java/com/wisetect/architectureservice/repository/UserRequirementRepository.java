package com.wisetect.architectureservice.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.wisetect.architectureservice.domain.model.UserRequirement;

import reactor.core.publisher.Flux;

@Repository
public interface UserRequirementRepository extends R2dbcRepository<UserRequirement, Long> {
    Flux<UserRequirement> findByUserId(String userId);
}
