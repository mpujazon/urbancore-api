package com.urbancore.urbancore_api.dtos;

import com.urbancore.urbancore_api.models.IncidentCategory;
import com.urbancore.urbancore_api.models.IncidentPriority;
import com.urbancore.urbancore_api.models.IncidentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Admin incident summary used in backoffice paginated list")
public record AdminIncidentListItemDto(
        @Schema(description = "Unique incident identifier (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,
        @Schema(description = "Short title summarizing the incident", example = "Broken street light near school")
        String title,
        @Schema(description = "Incident category", example = "LIGHTING")
        IncidentCategory category,
        @Schema(description = "Current lifecycle status", example = "NEW")
        IncidentStatus status,
        @Schema(description = "Administrative priority level", example = "HIGH")
        IncidentPriority priority,
        @Schema(description = "City identifier the incident belongs to", example = "barcelona")
        String cityId,
        @Schema(description = "Reporter user identifier, if available", example = "42", nullable = true)
        String reporterId,
        @Schema(description = "Reporter display name (email fallback), if available", example = "marta.soler@example.com", nullable = true)
        String reporterDisplayName,
        @Schema(description = "Thumbnail URL of the first attached image, or null", nullable = true)
        String thumbnailUrl,
        @Schema(description = "ISO-8601 creation timestamp", example = "2026-05-19T09:20:00Z")
        String createdAt,
        @Schema(description = "ISO-8601 last update timestamp", example = "2026-05-19T09:20:00Z")
        String updatedAt,
        @Schema(description = "Number of planned actions linked to this incident", example = "0")
        long linkedPlannedActionsCount
) {
}
