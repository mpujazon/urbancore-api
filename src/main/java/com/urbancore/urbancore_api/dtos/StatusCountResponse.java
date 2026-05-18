package com.urbancore.urbancore_api.dtos;

import com.urbancore.urbancore_api.models.IncidentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Incident count grouped by status")
public record StatusCountResponse(
        @Schema(description = "Incident status", example = "NEW")
        IncidentStatus status,

        @Schema(description = "Number of incidents for this status", example = "24")
        long count
) {
}
