package com.urbancore.urbancore_api.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Sort field and direction")
public record SortDto(
        @Schema(description = "Entity field name used for sorting", example = "createdAt")
        String field,

        @Schema(description = "Sort direction", example = "DESC")
        String direction
) {
}
