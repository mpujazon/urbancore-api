package com.urbancore.urbancore_api.controllers;

import com.urbancore.urbancore_api.dtos.ApiErrorResponse;
import com.urbancore.urbancore_api.dtos.IncidentStatsFilter;
import com.urbancore.urbancore_api.dtos.IncidentStatsSummaryResponse;
import com.urbancore.urbancore_api.models.IncidentCategory;
import com.urbancore.urbancore_api.models.IncidentStatus;
import com.urbancore.urbancore_api.services.IncidentStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/stats")
@Tag(name = "Statistics", description = "Public statistics for UrbanCore dashboards")
public class StatsController {

    private final IncidentStatsService incidentStatsService;

    public StatsController(IncidentStatsService incidentStatsService) {
        this.incidentStatsService = incidentStatsService;
    }

    @GetMapping("/incidents/summary")
    @Operation(
            summary = "Get public incident statistics summary",
            description = """
                    Returns aggregated, public-safe incident metrics for dashboard KPIs and charts. \
                    This endpoint is public and never exposes reporter identity, emails, internal notes, or any private user data. \
                    Filters are optional and can be combined; when omitted, global public statistics are returned.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Aggregated incident summary for dashboard widgets and charts",
                    content = @Content(schema = @Schema(implementation = IncidentStatsSummaryResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid query parameters (e.g. from > to or invalid enum)",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public IncidentStatsSummaryResponse getIncidentSummary(
            @RequestParam(required = false)
            @Parameter(description = "Filter by city identifier", example = "city_bcn")
            String cityId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "Filter incidents created from this instant (inclusive)", example = "2026-05-01T00:00:00Z")
            Instant from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "Filter incidents created until this instant (inclusive)", example = "2026-05-31T23:59:59Z")
            Instant to,

            @RequestParam(required = false)
            @Parameter(description = "Filter by incident category", example = "LIGHTING")
            IncidentCategory category,

            @RequestParam(required = false)
            @Parameter(description = "Filter by incident status", example = "NEW")
            IncidentStatus status
    ) {
        IncidentStatsFilter filter = new IncidentStatsFilter(cityId, from, to, category, status);
        return incidentStatsService.getIncidentSummary(filter);
    }
}
