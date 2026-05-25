package com.urbancore.urbancore_api.incident.dto;

import com.urbancore.urbancore_api.incident.entity.IncidentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A single entry in the incident status history timeline")
public record IncidentStatusHistoryDto(
        @Schema(description = "History entry identifier", example = "h-01abc")
        String id,

        @Schema(description = "Previous status (nullable for the first transition)", example = "NEW", nullable = true)
        IncidentStatus fromStatus,

        @Schema(description = "New status after transition", example = "UNDER_REVIEW")
        IncidentStatus toStatus,

        @Schema(description = "User ID or system actor that performed the change", example = "42")
        String changedBy,

        @Schema(description = "Optional reason for the status change", example = "Admin review started")
        String reason,

        @Schema(description = "ISO-8601 timestamp of the change", example = "2026-04-16T10:30:00Z")
        String changedAt
) {
}
