package com.wisetect.userservice.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.wisetect.userservice.domain.dto.CreateUserRequest;
import com.wisetect.userservice.domain.dto.UserProfileUpdateRequest;
import com.wisetect.userservice.domain.model.User;
import com.wisetect.userservice.service.UserService;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;

    private UserController userController;

    @BeforeEach
    void setUp() {

    }

    @Test
    void method_name() {
    }

}
