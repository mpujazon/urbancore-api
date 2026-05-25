package com.urbancore.urbancore_api.plannedaction.dto;

import com.urbancore.urbancore_api.plannedaction.entity.PlannedActionStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Public-safe planned action item for calendar view")
public record PublicPlannedActionCalendarItemResponse(
        @Schema(description = "Planned action identifier", example = "pa_123")
        UUID id,

        @Schema(description = "Planned action title", example = "Replace damaged bench")
        String title,

        @Schema(description = "Planned action summary", example = "Scheduled maintenance for public furniture")
        String description,

        @Schema(description = "Planned action status", example = "PLANNED")
        PlannedActionStatus status,

        @Schema(description = "Planned start timestamp", example = "2026-05-22T08:00:00Z")
        Instant scheduledStart,

        @Schema(description = "Planned end timestamp", example = "2026-05-22T11:00:00Z", nullable = true)
        Instant scheduledEnd,

        @Schema(description = "Public incident reference for navigation")
        PublicPlannedActionIncidentRefResponse incident
) {
}
