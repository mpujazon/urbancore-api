package com.urbancore.urbancore_api.dtos;

import com.urbancore.urbancore_api.models.IncidentPriority;
import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateIncidentPriorityRequest(
        @Schema(description = "New administrative priority level", example = "HIGH")
        IncidentPriority priority
) {
}
