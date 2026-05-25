package com.urbancore.urbancore_api.incident.dto;

import com.urbancore.urbancore_api.auth.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Public-safe reporter information")
public record IncidentReporterDto(
        @Schema(description = "Reporter user identifier", example = "42")
        String id,

        @Schema(description = "Display name (email)", example = "citizen@example.com")
        String displayName,

        @Schema(description = "Reporter role", example = "ROLE_CITIZEN")
        UserRole role
) {
}
