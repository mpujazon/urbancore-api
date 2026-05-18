package com.urbancore.urbancore_api.dtos;

import com.urbancore.urbancore_api.models.PlannedActionStatus;

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

