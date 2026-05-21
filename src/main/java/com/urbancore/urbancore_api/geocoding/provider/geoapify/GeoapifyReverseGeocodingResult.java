package com.urbancore.urbancore_api.geocoding.provider.geoapify;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GeoapifyReverseGeocodingResult(
        Double lat,
        @JsonProperty("lon") Double lng,
        @JsonProperty("formatted") String addressLabel,
        String street,
        @JsonProperty("housenumber") String houseNumber,
        String postcode,
        String city,
        String country,
        String suburb,
        @JsonProperty("country_code") String countryCode
) {
}
