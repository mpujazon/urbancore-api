package com.urbancore.urbancore_api.dtos;

import com.urbancore.urbancore_api.models.IncidentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Public-safe status transition entry for an incident")
public record PublicIncidentStatusHistoryResponse(
        @Schema(description = "History entry identifier", example = "h-01abc")
        String id,

        @Schema(description = "Previous status", example = "NEW")
        IncidentStatus fromStatus,

        @Schema(description = "New status", example = "UNDER_REVIEW")
        IncidentStatus toStatus,

        @Schema(description = "When the transition happened", example = "2026-04-16T10:30:00Z")
        Instant changedAt
) {
}
