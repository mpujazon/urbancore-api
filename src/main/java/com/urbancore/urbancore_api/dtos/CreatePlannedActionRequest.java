package com.urbancore.urbancore_api.dtos;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public record CreatePlannedActionRequest(
    @NotNull
    UUID incidentId,

    @NotBlank
    @Size(min = 3, max = 120)
    String title,

    @Size(max = 2000)
    String description,

    @NotNull
    @FutureOrPresent
    Instant scheduledStart,
    
    Instant scheduledEnd,
    
    UUID assignedToUserId
) {
}
