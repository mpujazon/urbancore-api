package com.urbancore.urbancore_api.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Public-safe geolocation details for incident detail page")
public record PublicIncidentLocationResponse(
        @Schema(description = "Latitude coordinate", example = "41.3874")
        double lat,

        @Schema(description = "Longitude coordinate", example = "2.1686")
        double lng,

        @Schema(description = "Human-readable address label", example = "Carrer de Balmes, 42")
        String addressLabel,

        @Schema(description = "City name", example = "Barcelona")
        String city
) {
}
