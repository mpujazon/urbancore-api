package com.urbancore.urbancore_api.geocoding.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Normalized reverse geocoding response")
public record ReverseGeocodingResponse(
        @Schema(description = "Latitude used for reverse geocoding", example = "41.3874")
        double lat,
        @Schema(description = "Longitude used for reverse geocoding", example = "2.1686")
        double lng,
        @Schema(description = "Human-readable full address label", example = "Placa de Catalunya, 1, 08002 Barcelona, Spain")
        String addressLabel,
        @Schema(description = "Human-readable address label", example = "Placa de Catalunya, 1")
        String addressLine1,
        @Schema(description = "Street name", example = "Placa de Catalunya")
        String street,
        @Schema(description = "Street house number", example = "1")
        String houseNumber,
        @Schema(description = "Postal code", example = "08002")
        String postcode,
        @Schema(description = "City or locality", example = "Barcelona")
        String city,
        @Schema(description = "URL-safe city slug prefixed with ISO 3166-1 alpha-2 country code", example = "es-barcelona")
        String citySlug,
        @Schema(description = "Country name", example = "Spain")
        String country,
        @Schema(description = "ISO 3166-1 alpha-2 country code", example = "es")
        String countryCode,
        @Schema(description = "Suburb name", example = "el Parc i la Llacuna del Poblenou")
        String suburb,
        @Schema(description = "Geocoding provider used by backend", example = "geoapify")
        String provider
) {
}
