package com.urbancore.urbancore_api.stats.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Incident count grouped by area")
public record AreaCountResponse(
        @Schema(description = "Area label used for map/chart grouping", example = "Eixample")
        String area,

        @Schema(description = "Number of incidents in this area", example = "42")
        long count
) {
}
