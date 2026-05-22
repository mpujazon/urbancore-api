package com.urbancore.urbancore_api.dtos;

import com.urbancore.urbancore_api.models.IncidentCategory;
import com.urbancore.urbancore_api.models.IncidentPriority;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Payload to create a new urban incident")
public record CreateIncidentRequest(
        @Schema(description = "Short title summarizing the incident", example = "Pothole on Main Street")
        String title,

        @Schema(description = "Detailed description of the issue", example = "Large pothole ~50cm wide, dangerous for cyclists")
        String description,

        @Schema(description = "Incident category", example = "POTHOLE")
        IncidentCategory category,

        @Schema(description = "Suggested priority (defaults to UNDEFINED if omitted)", example = "MEDIUM")
        IncidentPriority priority,

        @Schema(description = "City UUID where the incident occurred. If omitted, the backend resolves or creates the city using citySlug.", example = "2f3c7a4e-9d2b-4f16-a51d-9d4b2f6e0c12")
        String cityId,

        @Schema(description = "URL-safe city slug used to find or create the city when cityId is omitted", example = "es-barcelona")
        String citySlug,

        @Schema(description = "Geolocation details of the incident")
        IncidentLocationDto location,

        @Schema(description = "Images uploaded via Cloudinary prior to creating the incident")
        List<IncidentImageDto> images
) {
}
