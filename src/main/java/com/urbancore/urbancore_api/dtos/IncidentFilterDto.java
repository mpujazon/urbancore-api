package com.urbancore.urbancore_api.dtos;

import com.urbancore.urbancore_api.models.IncidentCategory;
import com.urbancore.urbancore_api.models.IncidentPriority;
import com.urbancore.urbancore_api.models.IncidentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Query filters for listing incidents (all optional)")
public record IncidentFilterDto(
        @Schema(description = "Filter by incident status", example = "NEW")
        IncidentStatus status,

        @Schema(description = "Filter by incident category", example = "POTHOLE")
        IncidentCategory category,

        @Schema(description = "Filter by priority level", example = "HIGH")
        IncidentPriority priority,

        @Schema(description = "Filter by city identifier", example = "bcn-001")
        String cityId,

        @Schema(description = "Filter incidents created after this ISO-8601 instant", example = "2026-01-01T00:00:00Z")
        Instant from,

        @Schema(description = "Filter incidents created before this ISO-8601 instant", example = "2026-04-01T00:00:00Z")
        Instant to,

        @Schema(description = "Free-text search across title and description", example = "pothole")
        String q
) {
}
