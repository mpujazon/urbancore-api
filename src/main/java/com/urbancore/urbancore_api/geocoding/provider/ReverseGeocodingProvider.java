package com.urbancore.urbancore_api.geocoding.provider;

import com.urbancore.urbancore_api.geocoding.dto.ReverseGeocodingResponse;

public interface ReverseGeocodingProvider {

    ReverseGeocodingResponse reverse(double lat, double lng);
}
