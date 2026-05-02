package com.urbancore.urbancore_api.dtos;

import com.urbancore.urbancore_api.models.IncidentStatus;

public record IncidentStatusHistoryDto(
        String id,
        IncidentStatus fromStatus,
        IncidentStatus toStatus,
        String changedBy,
        String reason,
        String changedAt
) {
}
