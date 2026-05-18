package com.urbancore.urbancore_api.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import com.urbancore.urbancore_api.models.PlannedActionStatus;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Public-safe planned action linked to an incident")
public record PublicPlannedActionResponse(
        @Schema(description = "Planned action identifier", example = "0bf9f563-40f6-4f39-b580-f857f273f553")
        UUID id,

        @Schema(description = "Incident identifier", example = "550e8400-e29b-41d4-a716-446655440000")
        String incidentId,

        @Schema(description = "Short action title", example = "Temporary road signalization")
        String title,

        @Schema(description = "Detailed action description", example = "Install temporary signs and mark the area")
        String description,

        @Schema(description = "Current planned action status", example = "CONFIRMED")
        PlannedActionStatus status,

        @Schema(description = "Planned action start timestamp", example = "2026-05-15T08:00:00Z")
        Instant scheduledStart,

        @Schema(description = "Planned action end timestamp", example = "2026-05-15T10:00:00Z")
        Instant scheduledEnd,

        @Schema(description = "Assigned admin user internal id", example = "42", nullable = true)
        Long assignedToUserId,

        @Schema(description = "Creator user internal id", example = "2")
        Long createdBy,

        @Schema(description = "Creation timestamp", example = "2026-05-08T12:00:00Z")
        Instant createdAt,

        @Schema(description = "Last update timestamp", example = "2026-05-08T12:00:00Z")
        Instant updatedAt
) {
}
