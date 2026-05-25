package com.urbancore.urbancore_api.incident.dto;

import com.urbancore.urbancore_api.incident.entity.IncidentCategory;
import com.urbancore.urbancore_api.incident.entity.IncidentPriority;
import com.urbancore.urbancore_api.incident.entity.IncidentStatus;
import com.urbancore.urbancore_api.plannedaction.dto.PlannedActionResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Admin incident detail response for backoffice operational view")
public record AdminIncidentDetailResponse(
        @Schema(description = "Unique incident identifier (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,
        @Schema(description = "Short title summarizing the incident", example = "Broken street light near school")
        String title,
        @Schema(description = "Detailed description of the issue")
        String description,
        @Schema(description = "Incident category", example = "LIGHTING")
        IncidentCategory category,
        @Schema(description = "Current lifecycle status", example = "UNDER_REVIEW")
        IncidentStatus status,
        @Schema(description = "Administrative priority level", example = "HIGH")
        IncidentPriority priority,
        @Schema(description = "City identifier the incident belongs to", example = "barcelona")
        String cityId,
        @Schema(description = "Reporter summary, if available")
        IncidentReporterDto reporter,
        @Schema(description = "Geolocation details")
        IncidentLocationDto location,
        @Schema(description = "Images attached to the incident")
        List<IncidentImageDto> images,
        @Schema(description = "Planned actions linked to this incident")
        List<PlannedActionResponse> plannedActions,
        @Schema(description = "Chronological status transitions")
        List<IncidentStatusHistoryDto> statusHistory,
        @Schema(description = "ISO-8601 creation timestamp", example = "2026-05-19T09:20:00Z")
        String createdAt,
        @Schema(description = "ISO-8601 last update timestamp", example = "2026-05-19T09:20:00Z")
        String updatedAt
) {
}
