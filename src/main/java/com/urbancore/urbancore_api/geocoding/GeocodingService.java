package com.urbancore.urbancore_api.geocoding;

import com.urbancore.urbancore_api.geocoding.dto.ReverseGeocodingResponse;
import com.urbancore.urbancore_api.geocoding.provider.ReverseGeocodingProvider;
import org.springframework.stereotype.Service;

@Service
public class GeocodingService {

    private final CoordinatesValidator coordinatesValidator;
    private final ReverseGeocodingProvider reverseGeocodingProvider;

    public GeocodingService(CoordinatesValidator coordinatesValidator,
                            ReverseGeocodingProvider reverseGeocodingProvider) {
        this.coordinatesValidator = coordinatesValidator;
        this.reverseGeocodingProvider = reverseGeocodingProvider;
    }

    public ReverseGeocodingResponse reverse(double lat, double lng) {
        coordinatesValidator.validate(lat, lng);
        return reverseGeocodingProvider.reverse(lat, lng);
    }
}
