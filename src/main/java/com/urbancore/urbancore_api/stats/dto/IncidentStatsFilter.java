package com.urbancore.urbancore_api.stats.dto;

import com.urbancore.urbancore_api.incident.entity.IncidentCategory;
import com.urbancore.urbancore_api.incident.entity.IncidentStatus;

import java.time.Instant;

public record IncidentStatsFilter(
        String cityId,
        Instant from,
        Instant to,
        IncidentCategory category,
        IncidentStatus status
) {
    public String normalizedCityId() {
        if (cityId == null) {
            return null;
        }
        String trimmed = cityId.trim();
        return trimmed.isBlank() ? null : trimmed;
    }
}
