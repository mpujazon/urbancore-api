package com.urbancore.urbancore_api.city.controller;

import com.urbancore.urbancore_api.common.dto.ApiErrorResponse;
import com.urbancore.urbancore_api.city.dto.CityResponse;
import com.urbancore.urbancore_api.city.service.CityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/cities", "/api/cities"})
@Tag(name = "Cities", description = "Public city catalog used by frontend map and filters")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    @Operation(
            summary = "List supported cities",
            description = "Returns the public list of supported cities for FE integration. Public endpoint; no authentication required. Each city includes a stable id, name, and slug."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Supported cities",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CityResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public List<CityResponse> findAll() {
        return cityService.findAll();
    }
}
