package com.urbancore.urbancore_api.geocoding.provider.geoapify;

import com.urbancore.urbancore_api.geocoding.dto.ReverseGeocodingResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GeoapifyReverseGeocodingMapperTest {

    private final GeoapifyReverseGeocodingMapper mapper = new GeoapifyReverseGeocodingMapper();

    @Test
    void mapsGeoapifyResultToNormalizedDto() {
        GeoapifyReverseGeocodingResult source = new GeoapifyReverseGeocodingResult(
                41.3874,
                2.1686,
                "Placa de Catalunya, 1, 08002 Barcelona, Spain",
                "Placa de Catalunya",
                "1",
                "08002",
                "Barcelona",
                "Spain",
                "es"
        );

        ReverseGeocodingResponse result = mapper.toNormalized(41.3874, 2.1686, source);

        assertEquals(41.3874, result.lat());
        assertEquals(2.1686, result.lng());
        assertEquals("Placa de Catalunya, 1, 08002 Barcelona, Spain", result.addressLabel());
        assertEquals("Placa de Catalunya", result.street());
        assertEquals("1", result.houseNumber());
        assertEquals("08002", result.postcode());
        assertEquals("Barcelona", result.city());
        assertEquals("Spain", result.country());
        assertEquals("es", result.countryCode());
        assertEquals("geoapify", result.provider());
    }

    @Test
    void fallsBackToRequestedCoordinatesWhenProviderDoesNotReturnCoordinates() {
        GeoapifyReverseGeocodingResult source = new GeoapifyReverseGeocodingResult(
                null,
                null,
                "Address",
                null,
                null,
                null,
                null,
                null,
                null
        );

        ReverseGeocodingResponse result = mapper.toNormalized(40.0, 3.0, source);

        assertEquals(40.0, result.lat());
        assertEquals(3.0, result.lng());
    }
}
