package com.wisetect.architectureservice.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.wisetect.architectureservice.service.ArchitectureService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private ArchitectureService architectureService;

    private UserController userController;

    @BeforeEach
    void setUp() {

    }

    @Test
    void method_name() {
    }

}
