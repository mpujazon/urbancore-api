package com.urbancore.urbancore_api.dtos;

import com.urbancore.urbancore_api.models.IncidentCategory;
import com.urbancore.urbancore_api.models.IncidentPriority;
import com.urbancore.urbancore_api.models.IncidentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Public-safe incident summary used in paginated lists")
public record IncidentListItemDto(
        @Schema(description = "Unique incident identifier (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,

        @Schema(description = "Short title summarizing the incident", example = "Broken street light")
        String title,

        @Schema(description = "Incident category", example = "LIGHTING")
        IncidentCategory category,

        @Schema(description = "Current lifecycle status", example = "NEW")
        IncidentStatus status,

        @Schema(description = "Administrative priority level", example = "MEDIUM")
        IncidentPriority priority,

        @Schema(description = "City identifier the incident belongs to", example = "city_bcn")
        String cityId,

        @Schema(description = "Thumbnail URL of the first attached image, or null", example = "https://res.cloudinary.com/urbancore/image/upload/c_thumb,w_200/v1/users/42/incident-uploads/photo.jpg")
        String thumbnailUrl,

        @Schema(description = "Geolocation details")
        IncidentLocationDto location,

        @Schema(description = "ISO-8601 creation timestamp", example = "2026-04-16T10:25:30Z")
        String createdAt,

        @Schema(description = "ISO-8601 last update timestamp", example = "2026-04-16T10:25:30Z")
        String updatedAt
) {
}
