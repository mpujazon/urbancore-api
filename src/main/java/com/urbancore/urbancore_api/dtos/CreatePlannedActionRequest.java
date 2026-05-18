package com.urbancore.urbancore_api.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CreatePlannedActionRequest(
    @Schema(description = "Incident identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    @NotNull
    String incidentId,

    @Schema(description = "Short action title", example = "Replace damaged lamp")
    @NotBlank
    @Size(min = 3, max = 120)
    String title,

    @Schema(description = "Detailed action description", example = "Dispatch maintenance crew and replace full lamp head")
    @Size(max = 2000)
    String description,

    @Schema(description = "Planned action start timestamp", example = "2026-06-10T08:00:00Z")
    @NotNull
    Instant scheduledStart,

    @Schema(description = "Planned action end timestamp", example = "2026-06-10T10:00:00Z")
    Instant scheduledEnd,

    @Schema(description = "Assigned admin user internal id", example = "42")
    Long assignedToUserId
) {
}
