package com.wisetect.architectureservice.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wisetect.architectureservice.service.ArchitectureService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Management", description = "Endpoints for managing user data")
public class UserController {

    private final ArchitectureService architectureService;

    public UserController(ArchitectureService architectureService) {
        this.architectureService = architectureService;
    }

}
