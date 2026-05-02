package com.urbancore.urbancore_api.dtos;

import com.urbancore.urbancore_api.models.UserRole;

public record IncidentReporterDto(
        String id,
        String displayName,
        UserRole role
) {
}
