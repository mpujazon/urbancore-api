package com.urbancore.urbancore_api.controllers;

import com.urbancore.urbancore_api.dtos.AdminIncidentDetailResponse;
import com.urbancore.urbancore_api.dtos.AdminIncidentListItemDto;
import com.urbancore.urbancore_api.dtos.ApiErrorResponse;
import com.urbancore.urbancore_api.dtos.PagedResponseDto;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/incidents")
@Tag(name = "Admin Incidents", description = "Admin backoffice endpoints for listing and managing incidents")
public class AdminIncidentController {

    private final IncidentService incidentService;

    public AdminIncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @GetMapping
    @Operation(
            summary = "List incidents for admin backoffice",
            description = """
                    Returns paginated incidents for /admin/incidents with server-side filters and sorting. \
                    Requires Bearer JWT with ROLE_ADMIN. \
                    Filters are combined with AND logic. \
                    Search is case-insensitive and matches title, incident id, reporter id and reporter email.
                    """,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin incident list page", content = @Content(schema = @Schema(implementation = PagedResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid query parameters", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Requires ROLE_ADMIN", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public PagedResponseDto<AdminIncidentListItemDto> getAdminIncidents(
            @RequestParam(defaultValue = "0")
            @Parameter(description = "Zero-based page index", example = "0")
            int page,

            @RequestParam(defaultValue = "10")
            @Parameter(description = "Items per page. Allowed values: 10, 25, 50", example = "10")
            int size,

            @RequestParam(defaultValue = "createdAt,desc")
            @Parameter(description = "Sort in field,direction format. Allowed fields: createdAt, title, category, priority, status", example = "createdAt,desc")
            String sort,

            @RequestParam(required = false)
            @Parameter(description = "Free text search by title, incident id, reporter id or reporter email", example = "light")
            String search,

            @RequestParam(required = false)
            @Parameter(description = "Filter by incident status", example = "NEW")
            IncidentStatus status,

            @RequestParam(required = false)
            @Parameter(description = "Filter by incident category", example = "LIGHTING")
            IncidentCategory category,

            @RequestParam(required = false)
            @Parameter(description = "Filter by incident priority", example = "HIGH")
            IncidentPriority priority,

            @RequestParam(required = false)
            @Parameter(description = "Inclusive lower bound for createdAt. Accepts ISO-8601 date-time or yyyy-MM-dd", example = "2026-05-01")
            String dateFrom,

            @RequestParam(required = false)
            @Parameter(description = "Inclusive upper bound for createdAt. Accepts ISO-8601 date-time or yyyy-MM-dd", example = "2026-05-19")
            String dateTo
    ) {
        return incidentService.getAdminIncidents(page, size, sort, search, status, category, priority, dateFrom, dateTo);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get admin incident detail",
            description = "Returns operational incident detail for /admin/incidents/:id, including reporter, images, planned actions and status history. Requires ROLE_ADMIN.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin incident detail", content = @Content(schema = @Schema(implementation = AdminIncidentDetailResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Requires ROLE_ADMIN", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Incident not found", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public AdminIncidentDetailResponse getAdminIncidentById(
            @PathVariable
            @Parameter(description = "Incident identifier (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
            String id
    ) {
        return incidentService.getAdminIncidentDetailById(id);
    }
}
