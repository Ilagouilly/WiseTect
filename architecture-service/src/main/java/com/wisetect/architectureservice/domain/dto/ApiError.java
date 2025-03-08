package com.wisetect.architectureservice.domain.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private int status;
    private String error;
    private String message;
    private Instant timestamp;
    private String path;
    private String traceId;
    private List<String> errors;

    public ApiError() {
        this.traceId = UUID.randomUUID().toString();
        this.errors = new ArrayList<>();
    }
}
