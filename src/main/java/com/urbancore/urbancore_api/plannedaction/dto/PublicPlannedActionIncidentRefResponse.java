package com.urbancore.urbancore_api.plannedaction.dto;

import com.urbancore.urbancore_api.incident.entity.IncidentCategory;
import com.urbancore.urbancore_api.incident.entity.IncidentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Public incident reference for planned action calendar navigation")
public record PublicPlannedActionIncidentRefResponse(
        @Schema(description = "Incident identifier", example = "inc_456")
        String id,

        @Schema(description = "Incident title", example = "Broken bench near the park entrance")
        String title,

        @Schema(description = "Incident category", example = "STREET_FURNITURE")
        IncidentCategory category,

        @Schema(description = "Incident status", example = "PLANNED")
        IncidentStatus status,

        @Schema(description = "City identifier", example = "city_bcn")
        String cityId,

        @Schema(description = "Human-readable location", example = "Parc de la Ciutadella")
        String addressLabel,

        @Schema(description = "Latitude", example = "41.3888")
        Double lat,

        @Schema(description = "Longitude", example = "2.1870")
        Double lng
) {
}
