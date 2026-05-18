package com.urbancore.urbancore_api.dtos;

import com.urbancore.urbancore_api.models.PlannedActionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record UpdatePlannedActionRequest(
        @Schema(description = "Short action title", example = "Replace damaged lamp")
        @Size(min = 3, max = 120)
        String title,

        @Schema(description = "Detailed action description", example = "Dispatch maintenance crew and replace full lamp head")
        @Size(max = 2000)
        String description,

        @Schema(description = "Planned action start timestamp", example = "2026-06-10T08:00:00Z")
        Instant scheduledStart,

        @Schema(description = "Planned action end timestamp", example = "2026-06-10T10:00:00Z")
        Instant scheduledEnd,

        @Schema(description = "Assigned admin user internal id", example = "42")
        Long assignedToUserId,

        @Schema(description = "Current planned action status", example = "CONFIRMED")
        PlannedActionStatus status
) {
}
