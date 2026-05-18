package com.urbancore.urbancore_api.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Public dashboard summary with incident KPIs and chart datasets")
public record IncidentStatsSummaryResponse(
        @Schema(description = "Total incidents matching the selected filters", example = "128")
        long totalIncidents,

        @Schema(description = "Open incidents (all except RESOLVED, REJECTED, CANCELLED)", example = "74")
        long openIncidents,

        @Schema(description = "Resolved incidents", example = "38")
        long resolvedIncidents,

        @Schema(description = "Planned incidents", example = "16")
        long plannedIncidents,

        @Schema(description = "Average days between creation and last update for RESOLVED incidents. Null when there are no resolved incidents.", example = "4.6", nullable = true)
        Double averageResolutionDays,

        @Schema(description = "Dataset grouped by incident status")
        List<StatusCountResponse> byStatus,

        @Schema(description = "Dataset grouped by incident category")
        List<CategoryCountResponse> byCategory,

        @Schema(description = "Daily incident creation trend")
        List<DailyIncidentCountResponse> trend,

        @Schema(description = "Dataset grouped by area label")
        List<AreaCountResponse> byArea
) {
}
