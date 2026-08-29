package com.urbancore.urbancore_api.health.controller;

import com.urbancore.urbancore_api.health.dto.HealthResponse;
import com.urbancore.urbancore_api.health.service.HealthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/health", "/api/health"})
@Tag(name = "Health", description = "Liveness and readiness checks")
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping
    @Operation(
            summary = "Health check",
            description = "Returns 200 OK when the application is running. Does not check database connectivity to avoid waking the Neon free tier instance."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Application is healthy",
                    content = @Content(schema = @Schema(implementation = HealthResponse.class))
            )
    })
    public HealthResponse health() {
        return new HealthResponse("UP", "SKIPPED", healthService.now());
    }
}