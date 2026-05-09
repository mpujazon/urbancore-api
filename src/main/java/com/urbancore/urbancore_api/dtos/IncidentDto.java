package com.urbancore.urbancore_api.dtos;

import com.urbancore.urbancore_api.models.IncidentCategory;
import com.urbancore.urbancore_api.models.IncidentPriority;
import com.urbancore.urbancore_api.models.IncidentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Complete incident representation returned by the API")
public record IncidentDto(
        @Schema(description = "Unique incident identifier (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,

        @Schema(description = "Short title summarizing the incident", example = "Pothole on Main Street")
        String title,

        @Schema(description = "Detailed description of the issue", example = "Large pothole ~50cm wide, dangerous for cyclists")
        String description,

        @Schema(description = "Incident category", example = "POTHOLE")
        IncidentCategory category,

        @Schema(description = "Current lifecycle status", example = "NEW")
        IncidentStatus status,

        @Schema(description = "Administrative priority level", example = "MEDIUM")
        IncidentPriority priority,

        @Schema(description = "City identifier the incident belongs to", example = "bcn-001")
        String cityId,

        @Schema(description = "Reporter summary (public-safe)")
        IncidentReporterDto reporter,

        @Schema(description = "Geolocation details")
        IncidentLocationDto location,

        @Schema(description = "Images attached to the incident")
        List<IncidentImageDto> images,

        @Schema(description = "Planned actions linked to this incident")
        List<Object> plannedActions,

        @Schema(description = "Chronological status transitions")
        List<IncidentStatusHistoryDto> statusHistory,

        @Schema(description = "ISO-8601 creation timestamp", example = "2026-04-16T10:30:00Z")
        String createdAt,

        @Schema(description = "ISO-8601 last update timestamp", example = "2026-04-16T12:00:00Z")
        String updatedAt
) {
}
