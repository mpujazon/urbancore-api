package com.urbancore.urbancore_api.controllers;

import com.urbancore.urbancore_api.dtos.ApiErrorResponse;
import com.urbancore.urbancore_api.dtos.FieldErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final Pattern CODED_REASON_PATTERN = Pattern.compile("^([A-Z0-9_]+):\\s*(.+)$");

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatusException(
            ResponseStatusException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String traceId = UUID.randomUUID().toString().substring(0, 10);

        log.warn("ResponseStatusException [{}] {} on {}: {}",
                traceId, status.value(), request.getRequestURI(), ex.getReason());

        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now().toString(),
                status.value(),
                status.getReasonPhrase() != null ? status.getReasonPhrase() : ex.getClass().getSimpleName(),
                resolveErrorCode(status, ex.getReason()),
                resolveMessage(ex.getReason()),
                request.getRequestURI(),
                List.of(),
                traceId
        );

        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {
        String traceId = UUID.randomUUID().toString().substring(0, 10);
        String fieldName = ex.getName() != null ? ex.getName() : "query parameter";
        String message = "Invalid value for parameter '" + fieldName + "'";

        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            String acceptedValues = Arrays.stream(ex.getRequiredType().getEnumConstants())
                    .map(String::valueOf)
                    .filter(value -> !"NULL".equals(value))
                    .reduce((left, right) -> left + ", " + right)
                    .orElse("");
            message = "Invalid value for parameter '" + fieldName + "'. Allowed values: " + acceptedValues;
        }

        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "VALIDATION_FAILED",
                message,
                request.getRequestURI(),
                List.of(),
                traceId
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpServletRequest request
    ) {
        String traceId = UUID.randomUUID().toString().substring(0, 10);
        String message = "Missing required parameter '" + ex.getParameterName() + "'";

        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "VALIDATION_FAILED",
                message,
                request.getRequestURI(),
                List.of(),
                traceId
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(
            Exception ex,
            HttpServletRequest request
    ) {
        String traceId = UUID.randomUUID().toString().substring(0, 10);

        log.error("Unexpected error [{}] on {}: {}", traceId, request.getRequestURI(), ex.getMessage(), ex);

        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now().toString(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "INTERNAL_ERROR",
                "An unexpected error occurred",
                request.getRequestURI(),
                List.of(),
                traceId
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private String resolveErrorCode(HttpStatus status, String reason) {
        Matcher matcher = reason != null ? CODED_REASON_PATTERN.matcher(reason) : null;
        if (matcher != null && matcher.matches()) {
            return matcher.group(1);
        }

        if (status == HttpStatus.BAD_REQUEST) {
            return "VALIDATION_FAILED";
        }
        if (status == HttpStatus.UNAUTHORIZED) {
            return "UNAUTHORIZED";
        }
        if (status == HttpStatus.FORBIDDEN) {
            return "FORBIDDEN";
        }
        if (status == HttpStatus.NOT_FOUND) {
            return "RESOURCE_NOT_FOUND";
        }
        if (status == HttpStatus.CONFLICT) {
            return "CONFLICT";
        }
        return "ERROR";
    }

    private String resolveMessage(String reason) {
        if (reason == null) {
            return "An error occurred";
        }

        Matcher matcher = CODED_REASON_PATTERN.matcher(reason);
        if (matcher.matches()) {
            return matcher.group(2);
        }

        return reason;
    }
}
