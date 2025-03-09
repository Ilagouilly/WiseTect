package com.wisetect.architectureservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.wisetect.architectureservice.service.ArchitectureService;

@ExtendWith(MockitoExtension.class)
class ArchitectureControllerTest {

    @Mock
    private ArchitectureService architectureService;

    @InjectMocks
    private ArchitectureController architectureController;

    @BeforeEach
    void setUp() {

    }

}
