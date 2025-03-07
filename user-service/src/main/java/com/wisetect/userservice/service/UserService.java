package com.wisetect.userservice.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.wisetect.userservice.domain.dto.CreateUserRequest;
import com.wisetect.userservice.domain.dto.UserProfileUpdateRequest;
import com.wisetect.userservice.domain.model.User;
import com.wisetect.userservice.exception.ResourceNotFoundException;
import com.wisetect.userservice.exception.ValidationException;
import com.wisetect.userservice.repository.UserRepository;
import com.wisetect.userservice.utils.LoggingFormat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private static final int MAX_QUERY_LENGTH = 255;
}
