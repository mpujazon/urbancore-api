package com.urbancore.urbancore_api.geocoding.provider.geoapify;

import java.util.List;

public record GeoapifyReverseGeocodingResponse(
        List<GeoapifyReverseGeocodingResult> results
) {
}
