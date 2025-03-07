package com.wisetect.architectureservice.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.wisetect.architectureservice.domain.model.User;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, UUID> {

}
