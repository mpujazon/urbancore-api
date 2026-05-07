package com.urbancore.urbancore_api.controllers;

import com.urbancore.urbancore_api.dtos.ApiErrorResponse;
import com.urbancore.urbancore_api.dtos.CreateIncidentRequest;
import com.urbancore.urbancore_api.dtos.IncidentDto;
import com.urbancore.urbancore_api.dtos.IncidentFilterDto;
import com.urbancore.urbancore_api.models.IncidentCategory;
import com.urbancore.urbancore_api.models.IncidentPriority;
import com.urbancore.urbancore_api.models.IncidentStatus;
import com.urbancore.urbancore_api.services.IncidentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@Tag(name = "Incidents", description = "Create, list and search urban incidents")
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new incident",
            description = """
                    Creates an urban incident with initial status NEW. \
                    The caller must be authenticated with role CITIZEN. \
                    Images must be uploaded to Cloudinary before calling this endpoint; \
                    provide the returned Cloudinary identifiers in the images array.
                    """,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Incident created successfully",
                    content = @Content(schema = @Schema(implementation = IncidentDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body — missing required fields or invalid images",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing or invalid Firebase JWT",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Authenticated user does not have the CITIZEN role",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public IncidentDto createIncident(
            @RequestBody @Schema(implementation = CreateIncidentRequest.class)
            CreateIncidentRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return incidentService.createIncident(request, jwt);
    }

    @GetMapping
    @Operation(
            summary = "List incidents (public)",
            description = """
                    Returns all incidents matching optional filters. \
                    This endpoint is public — no authentication required. \
                    Results are ordered by creation date descending.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Filtered list of incidents",
                    content = @Content(schema = @Schema(implementation = IncidentDto.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public List<IncidentDto> getAllIncidents(
            @RequestParam(required = false)
            @Parameter(description = "Filter by incident status", example = "NEW")
            IncidentStatus status,

            @RequestParam(required = false)
            @Parameter(description = "Filter by incident category", example = "POTHOLE")
            IncidentCategory category,

            @RequestParam(required = false)
            @Parameter(description = "Filter by priority level", example = "HIGH")
            IncidentPriority priority,

            @RequestParam(required = false)
            @Parameter(description = "Filter by city identifier", example = "bcn-001")
            String cityId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "Filter incidents created after this ISO-8601 instant", example = "2026-01-01T00:00:00Z")
            Instant from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "Filter incidents created before this ISO-8601 instant", example = "2026-04-01T00:00:00Z")
            Instant to,

            @RequestParam(required = false)
            @Parameter(description = "Free-text search across title and description", example = "pothole")
            String q
    ) {
        IncidentFilterDto filters = new IncidentFilterDto(status, category, priority, cityId, from, to, q);
        return incidentService.getAllIncidents(filters);
    }

    @GetMapping("/me")
    @Operation(
            summary = "List current user's incidents",
            description = """
                    Returns all incidents created by the currently authenticated citizen. \
                    Requires CITIZEN role.
                    """,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Incidents created by the current user",
                    content = @Content(schema = @Schema(implementation = IncidentDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing or invalid Firebase JWT",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Authenticated user does not have the CITIZEN role",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public List<IncidentDto> getMyIncidents(@AuthenticationPrincipal Jwt jwt) {
        return incidentService.getCurrentCitizenIncidents(jwt);
    }
}
