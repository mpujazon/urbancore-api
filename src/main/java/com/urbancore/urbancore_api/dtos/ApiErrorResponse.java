package com.urbancore.urbancore_api.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Standard error response returned by all API endpoints")
public record ApiErrorResponse(
        @Schema(description = "ISO-8601 instant when the error occurred",
                example = "2026-04-16T10:30:00Z")
        String timestamp,

        @Schema(description = "HTTP status code", example = "400")
        int status,

        @Schema(description = "HTTP reason phrase", example = "Bad Request")
        String error,

        @Schema(description = "Application-specific error code",
                example = "INCIDENT_INVALID_PAYLOAD")
        String code,

        @Schema(description = "Human-readable message describing the error",
                example = "Description is required")
        String message,

        @Schema(description = "Request path that triggered the error",
                example = "/api/incidents")
        String path,

        @Schema(description = "Per-field validation errors (present on 400 responses)")
        List<FieldErrorResponse> fieldErrors,

        @Schema(description = "Unique trace identifier for log correlation",
                example = "9f5b0d9d2a")
        String traceId
) {
}
