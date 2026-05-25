package com.urbancore.urbancore_api.plannedaction.dto;

import com.urbancore.urbancore_api.plannedaction.entity.PlannedActionStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Detailed planned action response")
public record PlannedActionResponse(
        @Schema(description = "Planned action identifier", example = "0bf9f563-40f6-4f39-b580-f857f273f553")
        UUID id,
        @Schema(description = "Incident identifier", example = "550e8400-e29b-41d4-a716-446655440000")
        String incidentId,
        @Schema(description = "Short action title", example = "Replace damaged lamp")
        String title,
        @Schema(description = "Detailed action description", example = "Dispatch maintenance crew and replace full lamp head")
        String description,
        @Schema(description = "Current planned action status", example = "PLANNED")
        PlannedActionStatus status,
        @Schema(description = "Planned action start timestamp", example = "2026-06-10T08:00:00Z")
        Instant scheduledStart,
        @Schema(description = "Planned action end timestamp", example = "2026-06-10T10:00:00Z")
        Instant scheduledEnd,
        @Schema(description = "Assigned admin user internal id", example = "42", nullable = true)
        Long assignedToUserId,
        @Schema(description = "Creator user internal id", example = "42")
        Long createdBy,
        @Schema(description = "Creation timestamp", example = "2026-05-18T10:30:00Z")
        Instant createdAt,
        @Schema(description = "Last update timestamp", example = "2026-05-18T11:00:00Z")
        Instant updatedAt
) {
}
