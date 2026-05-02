package com.urbancore.urbancore_api.dtos;

import com.urbancore.urbancore_api.models.IncidentCategory;
import com.urbancore.urbancore_api.models.IncidentPriority;
import com.urbancore.urbancore_api.models.IncidentStatus;

import java.util.List;

public record IncidentDto(
        String id,
        String title,
        String description,
        IncidentCategory category,
        IncidentStatus status,
        IncidentPriority priority,
        String cityId,
        IncidentReporterDto reporter,
        IncidentLocationDto location,
        List<IncidentImageDto> images,
        List<Object> plannedActions,
        List<IncidentStatusHistoryDto> statusHistory,
        String createdAt,
        String updatedAt
) {
}
