package com.wisetect.architectureservice.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.wisetect.architectureservice.domain.model.RequirementQuestion;

import reactor.core.publisher.Flux;

@Repository
public interface RequirementQuestionRepository extends R2dbcRepository<RequirementQuestion, Long> {
    Flux<RequirementQuestion> findAllByOrderByDisplayOrderAsc();

    Flux<RequirementQuestion> findByCategoryOrderByDisplayOrderAsc(String category);
}
