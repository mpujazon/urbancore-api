package com.urbancore.urbancore_api.dtos;

public record IncidentLocationDto(
        double lat,
        double lng,
        String addressLabel,
        String area,
        String city,
        String geohash
) {
}
