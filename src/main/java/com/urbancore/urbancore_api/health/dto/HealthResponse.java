package com.urbancore.urbancore_api.health.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Health check response")
public record HealthResponse(
        @Schema(description = "Overall application status", example = "UP")
        String status,

        @Schema(description = "Database connectivity status (skipped on liveness to avoid waking the Neon free tier)", example = "SKIPPED")
        String database,

        @Schema(description = "ISO-8601 instant when the check was performed",
                example = "2026-08-19T12:00:00Z")
        String timestamp
) {
}