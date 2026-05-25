package com.urbancore.urbancore_api.city.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record CityResponse(
        @Schema(description = "Stable city UUID identifier", example = "2f3c7a4e-9d2b-4f16-a51d-9d4b2f6e0c12")
        UUID id,

        @Schema(description = "Human-readable city name", example = "Barcelona")
        String name,

        @Schema(description = "URL-safe city slug used to look up the city", example = "es-barcelona")
        String slug
) {
}
