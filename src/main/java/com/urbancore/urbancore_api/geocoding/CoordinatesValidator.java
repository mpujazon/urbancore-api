package com.urbancore.urbancore_api.geocoding;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CoordinatesValidator {

    public void validate(double lat, double lng) {
        if (lat < -90 || lat > 90) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "VALIDATION_FAILED: lat must be between -90 and 90"
            );
        }

        if (lng < -180 || lng > 180) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "VALIDATION_FAILED: lng must be between -180 and 180"
            );
        }
    }
}
