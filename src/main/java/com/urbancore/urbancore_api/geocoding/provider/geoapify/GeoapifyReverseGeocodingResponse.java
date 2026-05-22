package com.urbancore.urbancore_api.geocoding.provider.geoapify;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

public record GeoapifyReverseGeocodingResponse(
        List<GeoapifyReverseGeocodingResult> results
) {

    public String citySlug() {
        if (results == null || results.isEmpty()) {
            return null;
        }

        GeoapifyReverseGeocodingResult firstResult = results.getFirst();
        return toSlug(firstResult.countryCode(), firstResult.city());
    }

    private static String toSlug(String countryCode, String city) {
        if (countryCode == null || countryCode.isBlank() || city == null || city.isBlank()) {
            return null;
        }

        String countryPrefix = normalizeSlugPart(countryCode);
        String citySlug = normalizeSlugPart(city);

        if (countryPrefix == null || citySlug == null) {
            return null;
        }

        return countryPrefix + "-" + citySlug;
    }

    private static String normalizeSlugPart(String value) {
        String slug = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");

        return slug.isBlank() ? null : slug;
    }
}
