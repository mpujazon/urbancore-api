package com.urbancore.urbancore_api.controllers;

import com.urbancore.urbancore_api.dtos.ApiErrorResponse;
import com.urbancore.urbancore_api.dtos.CreatePlannedActionRequest;
import com.urbancore.urbancore_api.dtos.PlannedActionResponse;
import com.urbancore.urbancore_api.dtos.PublicPlannedActionCalendarItemResponse;
import com.urbancore.urbancore_api.dtos.UpdatePlannedActionRequest;
import com.urbancore.urbancore_api.models.PlannedActionStatus;
import com.urbancore.urbancore_api.models.User;
import com.urbancore.urbancore_api.services.CurrentUserService;
import com.urbancore.urbancore_api.services.PlannedActionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/planned-actions")
@Tag(name = "Planned Actions", description = "Admin management of planned actions linked to incidents")
public class PlannedActionController {

    private final PlannedActionService plannedActionService;
    private final CurrentUserService currentUserService;

    public PlannedActionController(PlannedActionService plannedActionService, CurrentUserService currentUserService) {
        this.plannedActionService = plannedActionService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create planned action",
            description = "Creates a planned action for an incident. Private endpoint for ROLE_ADMIN users. Incidents in CANCELLED, REJECTED, or RESOLVED cannot be modified.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Planned action created", content = @Content(schema = @Schema(implementation = PlannedActionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Requires ROLE_ADMIN", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Incident or user not found", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Incident cannot be modified in CANCELLED, REJECTED, or RESOLVED status", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public PlannedActionResponse create(
            @Valid @RequestBody CreatePlannedActionRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        User currentUser = currentUserService.getCurrentUser(jwt);
        return plannedActionService.create(request, currentUser.getId());
    }

    @GetMapping
    @Operation(
            summary = "List public planned actions for calendar",
            description = """
                    Returns public-safe planned actions for a date range.
                    This endpoint is public and does not require authentication.
                    It excludes internal/admin-only fields and only returns incident reference data needed for navigation.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Public planned actions found",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PublicPlannedActionCalendarItemResponse.class)))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid query parameters or invalid date range", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public List<PublicPlannedActionCalendarItemResponse> findByCityAndDateRange(
            @RequestParam(required = false)
            @Parameter(description = "Range start (ISO-8601 date or datetime)", example = "2026-05-01T00:00:00Z")
            String dateFrom,
            @RequestParam(required = false)
            @Parameter(description = "Range end (ISO-8601 date or datetime)", example = "2026-05-31T23:59:59Z")
            String dateTo,
            @RequestParam(required = false)
            @Parameter(description = "Optional planned action status filter", example = "PLANNED")
            PlannedActionStatus status
    ) {
        return plannedActionService.findPublicCalendarActions(dateFrom, dateTo, status);
    }

    @GetMapping("/incident/{incidentId}")
    @Operation(
            summary = "List planned actions by incident",
            description = "Returns all planned actions linked to an incident. Public endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Planned actions found", content = @Content(schema = @Schema(implementation = PlannedActionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public List<PlannedActionResponse> findByIncident(
            @PathVariable
            @Parameter(description = "Incident identifier", example = "550e8400-e29b-41d4-a716-446655440000")
            String incidentId
    ) {
        return plannedActionService.findByIncident(incidentId);
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Update planned action",
            description = "Updates editable fields and/or status for a planned action. Private endpoint for ROLE_ADMIN users. Incidents in CANCELLED, REJECTED, or RESOLVED cannot be modified.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Planned action updated", content = @Content(schema = @Schema(implementation = PlannedActionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Requires ROLE_ADMIN", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Planned action or assigned user not found", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Incident cannot be modified in CANCELLED, REJECTED, or RESOLVED status", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public PlannedActionResponse update(
            @PathVariable
            @Parameter(description = "Planned action id", example = "0bf9f563-40f6-4f39-b580-f857f273f553")
            UUID id,
            @Valid @RequestBody UpdatePlannedActionRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        User currentUser = currentUserService.getCurrentUser(jwt);
        return plannedActionService.update(id, request, currentUser.getId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete planned action",
            description = "Deletes a planned action. Private endpoint for ROLE_ADMIN users. Incidents in CANCELLED, REJECTED, or RESOLVED cannot be modified.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Planned action deleted"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Requires ROLE_ADMIN", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Planned action not found", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Incident cannot be modified in CANCELLED, REJECTED, or RESOLVED status", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public void delete(
            @PathVariable
            @Parameter(description = "Planned action id", example = "0bf9f563-40f6-4f39-b580-f857f273f553")
            UUID id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        User currentUser = currentUserService.getCurrentUser(jwt);
        plannedActionService.delete(id, currentUser.getId());
    }
}
