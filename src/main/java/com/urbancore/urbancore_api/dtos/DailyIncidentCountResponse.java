package com.urbancore.urbancore_api.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Incident count grouped by creation date")
public record DailyIncidentCountResponse(
        @Schema(description = "UTC date bucket", example = "2026-05-01")
        LocalDate date,

        @Schema(description = "Number of incidents created on this date", example = "6")
        long count
) {
}
