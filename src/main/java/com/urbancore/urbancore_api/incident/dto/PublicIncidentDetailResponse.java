package com.urbancore.urbancore_api.incident.dto;

import com.urbancore.urbancore_api.incident.entity.IncidentCategory;
import com.urbancore.urbancore_api.incident.entity.IncidentPriority;
import com.urbancore.urbancore_api.incident.entity.IncidentStatus;
import com.urbancore.urbancore_api.plannedaction.dto.PublicPlannedActionResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(description = "Public-safe incident detail response for the transparency page")
public record PublicIncidentDetailResponse(
        @Schema(description = "Unique incident identifier (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,

        @Schema(description = "Short title summarizing the incident", example = "Pothole on Main Street")
        String title,

        @Schema(description = "Detailed public description of the incident", example = "Large pothole near bus stop, dangerous for cyclists")
        String description,

        @Schema(description = "Incident category", example = "POTHOLE")
        IncidentCategory category,

        @Schema(description = "Current lifecycle status", example = "UNDER_REVIEW")
        IncidentStatus status,

        @Schema(description = "Current priority level", example = "HIGH")
        IncidentPriority priority,

        @Schema(description = "City identifier", example = "bcn-001")
        String cityId,

        @Schema(description = "Public-safe geolocation details")
        PublicIncidentLocationResponse location,

        @Schema(description = "Public-safe incident images")
        List<PublicIncidentImageResponse> images,

        @Schema(description = "Public-safe planned actions associated with this incident")
        List<PublicPlannedActionResponse> plannedActions,

        @Schema(description = "Public-safe status history timeline")
        List<PublicIncidentStatusHistoryResponse> statusHistory,

        @Schema(description = "ISO-8601 creation timestamp", example = "2026-04-16T10:30:00Z")
        Instant createdAt,

        @Schema(description = "ISO-8601 last update timestamp", example = "2026-04-16T12:00:00Z")
        Instant updatedAt
) {
}
