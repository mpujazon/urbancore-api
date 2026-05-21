package com.urbancore.urbancore_api.geocoding.provider.geoapify;

import com.urbancore.urbancore_api.geocoding.dto.ReverseGeocodingResponse;
import org.springframework.stereotype.Component;

@Component
public class GeoapifyReverseGeocodingMapper {

    private static final String PROVIDER = "geoapify";

    public ReverseGeocodingResponse toNormalized(double requestedLat, double requestedLng,
                                                 GeoapifyReverseGeocodingResult source) {
        return new ReverseGeocodingResponse(
                source.lat() != null ? source.lat() : requestedLat,
                source.lng() != null ? source.lng() : requestedLng,
                source.addressLabel(),
                source.addressLine1(),
                source.street(),
                source.houseNumber(),
                source.postcode(),
                source.city(),
                source.country(),
                source.countryCode(),
                source.suburb(),
                PROVIDER
        );
    }
}
