package com.urbancore.urbancore_api.ai.exception;

import org.springframework.http.HttpStatus;

public class AiSuggestionException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public AiSuggestionException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }
}
