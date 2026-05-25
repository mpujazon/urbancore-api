package com.urbancore.urbancore_api.geocoding.controller;

import com.urbancore.urbancore_api.common.dto.ApiErrorResponse;
import com.urbancore.urbancore_api.geocoding.GeocodingService;
import com.urbancore.urbancore_api.geocoding.dto.ReverseGeocodingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/geocoding")
@Tag(name = "Geocoding", description = "Public geocoding endpoints for coordinate/address conversion")
public class GeocodingController {

    private final GeocodingService geocodingService;

    public GeocodingController(GeocodingService geocodingService) {
        this.geocodingService = geocodingService;
    }

    @GetMapping("/reverse")
    @Operation(
            summary = "Reverse geocode coordinates",
            description = "Converts latitude/longitude to a normalized human-readable address. Public endpoint; no authentication required."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Normalized reverse geocoding result",
                    content = @Content(schema = @Schema(implementation = ReverseGeocodingResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid coordinates",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No address found for coordinates",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "502",
                    description = "Geoapify/network failure",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ReverseGeocodingResponse reverse(
            @RequestParam
            @Parameter(description = "Latitude in decimal degrees, between -90 and 90", example = "41.3874")
            double lat,
            @RequestParam
            @Parameter(description = "Longitude in decimal degrees, between -180 and 180", example = "2.1686")
            double lng
    ) {
        return geocodingService.reverse(lat, lng);
    }
}
