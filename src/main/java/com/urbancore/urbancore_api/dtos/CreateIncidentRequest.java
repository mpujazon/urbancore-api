package com.urbancore.urbancore_api.dtos;

import com.urbancore.urbancore_api.models.IncidentCategory;
import com.urbancore.urbancore_api.models.IncidentPriority;

import java.util.List;

public record CreateIncidentRequest(
        String title,
        String description,
        IncidentCategory category,
        IncidentPriority priority,
        String cityId,
        IncidentLocationDto location,
        List<IncidentImageDto> images
) {
}
