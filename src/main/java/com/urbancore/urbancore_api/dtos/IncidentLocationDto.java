package com.urbancore.urbancore_api.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Geolocation coordinates and address information")
public record IncidentLocationDto(
        @Schema(description = "Latitude coordinate", example = "41.3874")
        double lat,

        @Schema(description = "Longitude coordinate", example = "2.1686")
        double lng,

        @Schema(description = "Human-readable address label", example = "Carrer de Balmes, 42")
        String addressLabel,

        @Schema(description = "City name", example = "Barcelona")
        String city,

        @Schema(description = "Geohash representation for proximity queries", example = "sp3e1g")
        String geohash
) {
}
