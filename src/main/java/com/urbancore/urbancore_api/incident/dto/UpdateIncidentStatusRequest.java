package com.urbancore.urbancore_api.incident.dto;

import com.urbancore.urbancore_api.incident.entity.IncidentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public record UpdateIncidentStatusRequest(
        @Schema(description = "New incident lifecycle status", example = "UNDER_REVIEW")
        IncidentStatus status,

        @Schema(description = "Optional reason for status change", example = "Initial technical review started")
        @Size(max = 500)
        String reason
) {
}
