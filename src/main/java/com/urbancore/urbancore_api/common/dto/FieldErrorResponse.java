package com.urbancore.urbancore_api.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Single field-level validation error")
public record FieldErrorResponse(
        @Schema(description = "Name of the field that failed validation",
                example = "description")
        String field,

        @Schema(description = "Validation constraint message",
                example = "must not be blank")
        String message
) {
}
