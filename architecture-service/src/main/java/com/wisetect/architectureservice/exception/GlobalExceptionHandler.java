package com.wisetect.architectureservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wisetect.architectureservice.domain.dto.ApiError;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Order(-2)
@Configuration
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        LOGGER.error("Exception caught in GlobalExceptionHandler: {}", ex.getMessage(), ex);

        // Set the appropriate HTTP status
        HttpStatus status;
        String message;

        if (ex instanceof ResponseStatusException) {
            status = ((ResponseStatusException) ex).getStatusCode().is4xxClientError()
                    ? HttpStatus.valueOf(((ResponseStatusException) ex).getStatusCode().value())
                    : HttpStatus.INTERNAL_SERVER_ERROR;
            message = ex.getMessage();
        } else if (ex instanceof ValidationException) {
            status = HttpStatus.BAD_REQUEST;
            ValidationException validationEx = (ValidationException) ex;
            message = validationEx.getMessage();
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "An unexpected error occurred";
        }

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ApiError apiError = new ApiError();
        apiError.setStatus(status.value());
        apiError.setError(status.getReasonPhrase());
        apiError.setMessage(message);
        apiError.setTimestamp(java.time.Instant.now());

        DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
        DataBuffer dataBuffer;
        try {
            dataBuffer = bufferFactory.wrap(objectMapper.writeValueAsBytes(apiError));
        } catch (JsonProcessingException e) {
            LOGGER.error("Error writing response", e);
            dataBuffer = bufferFactory.wrap("".getBytes());
        }

        return exchange.getResponse().writeWith(Mono.just(dataBuffer));
    }
}
