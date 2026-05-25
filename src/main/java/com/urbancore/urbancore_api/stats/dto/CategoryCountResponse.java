package com.urbancore.urbancore_api.stats.dto;

import com.urbancore.urbancore_api.incident.entity.IncidentCategory;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Incident count grouped by category")
public record CategoryCountResponse(
        @Schema(description = "Incident category", example = "LIGHTING")
        IncidentCategory category,

        @Schema(description = "Number of incidents for this category", example = "35")
        long count
) {
}
