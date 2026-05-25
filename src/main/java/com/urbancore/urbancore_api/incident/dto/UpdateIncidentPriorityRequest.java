package com.urbancore.urbancore_api.incident.dto;

import com.urbancore.urbancore_api.incident.entity.IncidentPriority;
import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateIncidentPriorityRequest(
        @Schema(description = "New administrative priority level", example = "HIGH")
        IncidentPriority priority
) {
}
