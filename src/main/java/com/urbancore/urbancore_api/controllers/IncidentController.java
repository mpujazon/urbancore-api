package com.urbancore.urbancore_api.controllers;

import com.urbancore.urbancore_api.dtos.ApiErrorResponse;
import com.urbancore.urbancore_api.dtos.CreateIncidentRequest;
import com.urbancore.urbancore_api.dtos.IncidentDto;
import com.urbancore.urbancore_api.dtos.IncidentFilterDto;
import com.urbancore.urbancore_api.dtos.IncidentListItemDto;
import com.urbancore.urbancore_api.dtos.PagedResponseDto;
import com.urbancore.urbancore_api.dtos.PublicIncidentDetailResponse;
import com.urbancore.urbancore_api.dtos.UpdateIncidentPriorityRequest;
import com.urbancore.urbancore_api.dtos.UpdateIncidentStatusRequest;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
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
            summary = "List incidents with server-side pagination (public)",
            description = """
                    Returns a paginated list of incidents matching optional filters. \
                    This endpoint is public — no authentication required. \
                    Default sort is by creation date descending.

                    Pagination query parameters:
                    - page: zero-based page index (default 0)
                    - size: items per page (default 10, max 50)
                    - sort: sort field and direction in Spring format, e.g. createdAt,desc \
                    (allowed fields: createdAt, updatedAt, status, priority, category, title)

                    Filter query parameters:
                    - status: filter by incident status (e.g. NEW)
                    - category: filter by incident category (e.g. POTHOLE)
                    - priority: filter by priority level (e.g. HIGH)
                    - cityId: filter by city identifier (e.g. bcn-001)
                    - from: filter incidents created after this ISO-8601 instant
                    - to: filter incidents created before this ISO-8601 instant
                    - q: free-text search across title and description (case-insensitive)

                    Example: GET /api/incidents?page=0&size=10&sort=createdAt,desc&status=NEW&category=LIGHTING&cityId=city_bcn
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Paginated list of incidents matching the filters",
                    content = @Content(schema = @Schema(implementation = PagedResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination parameters (e.g. negative page)",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public PagedResponseDto<IncidentListItemDto> getAllIncidents(
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
            String q,

            @RequestParam(defaultValue = "0")
            @Parameter(description = "Zero-based page index", example = "0")
            int page,

            @RequestParam(defaultValue = "10")
            @Parameter(description = "Number of items per page (max 50)", example = "10")
            int size,

            @RequestParam(defaultValue = "createdAt,desc")
            @Parameter(description = "Sort field and direction (allowed fields: createdAt, updatedAt, status, priority, category, title)", example = "createdAt,desc")
            String sort
    ) {
        IncidentFilterDto filters = new IncidentFilterDto(status, category, priority, cityId, from, to, q);
        return incidentService.getAllIncidents(filters, page, size, sort);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get public incident detail",
            description = """
                    Returns a public-safe incident detail for transparency pages. \
                    This endpoint is public — no authentication required. \
                    It never exposes reporter identity, internal admin notes, or private backoffice-only data.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Public-safe incident detail",
                    content = @Content(schema = @Schema(implementation = PublicIncidentDetailResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Incident not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public PublicIncidentDetailResponse getPublicIncidentDetail(
            @PathVariable
            @Parameter(description = "Incident identifier (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
            String id
    ) {
        return incidentService.getPublicIncidentDetailById(id);
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
    public List<IncidentListItemDto> getCurrentCitizenIncidents(@AuthenticationPrincipal Jwt jwt) {
        return incidentService.getCurrentCitizenIncidents(jwt);
    }

    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Update incident status",
            description = """
                    Updates lifecycle status for an incident. Private endpoint for ROLE_ADMIN users. \
                    Also records a new status history entry when status changes.
                    """,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Incident status updated",
                    content = @Content(schema = @Schema(implementation = IncidentDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing or invalid Firebase JWT",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Requires ROLE_ADMIN",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Incident not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public IncidentDto updateIncidentStatus(
            @PathVariable
            @Parameter(description = "Incident identifier (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
            String id,
            @RequestBody @Schema(implementation = UpdateIncidentStatusRequest.class)
            UpdateIncidentStatusRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return incidentService.updateIncidentStatus(id, request, jwt);
    }

    @PatchMapping("/{id}/priority")
    @Operation(
            summary = "Update incident priority",
            description = "Updates administrative priority level for an incident. Private endpoint for ROLE_ADMIN users.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Incident priority updated",
                    content = @Content(schema = @Schema(implementation = IncidentDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing or invalid Firebase JWT",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Requires ROLE_ADMIN",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Incident not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public IncidentDto updateIncidentPriority(
            @PathVariable
            @Parameter(description = "Incident identifier (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
            String id,
            @RequestBody @Schema(implementation = UpdateIncidentPriorityRequest.class)
            UpdateIncidentPriorityRequest request
    ) {
        return incidentService.updateIncidentPriority(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete an incident",
            description = """
                    Deletes an incident by id. Requires Bearer JWT with role CITIZEN or ADMIN. \
                    Incident must be in NEW status to be deleted (for both ADMIN and CITIZEN). \
                    Citizen users can delete only their own incidents.
                    """,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Incident deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing or invalid Firebase JWT",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Authenticated user is not allowed to delete this incident",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Incident not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Incident can only be deleted when status is NEW",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public void deleteIncident(
            @PathVariable
            @Parameter(description = "Incident identifier (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
            String id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        incidentService.deleteIncident(id, jwt);
    }
}
