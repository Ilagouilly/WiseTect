package com.wisetect.architectureservice.exception;

public class LlmProcessingException extends RuntimeException {
    public LlmProcessingException(String message) {
        super(message);
    }

    public LlmProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
