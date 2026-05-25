package com.urbancore.urbancore_api.plannedaction.dto;

import com.urbancore.urbancore_api.plannedaction.entity.PlannedActionStatus;

import java.time.Instant;
import java.util.UUID;

public record CreatePlannedActionResponse(
    UUID id,
    UUID incidentId,
    String title,
    String description,
    PlannedActionStatus status,
    Instant scheduledStart,
    Instant scheduledEnd,
    UUID assignedToUserId,
    UUID createdBy,
    Instant createdAt,
    Instant updatedAt
) {
}

