package com.urbancore.urbancore_api.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Public-safe planned action linked to an incident")
public record PublicPlannedActionResponse(
        @Schema(description = "Planned action identifier", example = "pa-6f830d")
        String id,

        @Schema(description = "Public title of the planned action", example = "Temporary road signalization")
        String title,

        @Schema(description = "Public description of the action", example = "Install temporary signs and mark the area")
        String description,

        @Schema(description = "Current public status of the action", example = "PLANNED")
        String status,

        @Schema(description = "Planned execution date in ISO-8601", example = "2026-05-15T08:00:00Z")
        Instant plannedFor,

        @Schema(description = "When this action became publicly visible in ISO-8601", example = "2026-05-12T12:00:00Z")
        Instant publishedAt
) {
}
