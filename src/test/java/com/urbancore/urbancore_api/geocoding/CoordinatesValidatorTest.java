package com.urbancore.urbancore_api.geocoding;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CoordinatesValidatorTest {

    private final CoordinatesValidator validator = new CoordinatesValidator();

    @Test
    void acceptsValidCoordinates() {
        assertDoesNotThrow(() -> validator.validate(41.3874, 2.1686));
        assertDoesNotThrow(() -> validator.validate(-90, -180));
        assertDoesNotThrow(() -> validator.validate(90, 180));
    }

    @Test
    void rejectsLatitudeOutOfRange() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> validator.validate(90.0001, 2.1686));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("VALIDATION_FAILED: lat must be between -90 and 90", ex.getReason());
    }

    @Test
    void rejectsLongitudeOutOfRange() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> validator.validate(41.3874, -180.0001));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("VALIDATION_FAILED: lng must be between -180 and 180", ex.getReason());
    }
}
