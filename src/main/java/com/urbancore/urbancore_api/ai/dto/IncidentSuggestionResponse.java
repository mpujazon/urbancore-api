package com.urbancore.urbancore_api.ai.dto;

import com.urbancore.urbancore_api.incident.entity.IncidentCategory;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "AI suggestion response for incident form autocompletion")
public record IncidentSuggestionResponse(
        @Schema(description = "Suggested incident title", example = "Farola rota en la acera")
        String title,

        @Schema(description = "Suggested incident description", example = "Se observa una farola danada o apagada en una zona peatonal.")
        String description,

        @Schema(description = "Suggested incident category", example = "LIGHTING")
        IncidentCategory category
) {
}
