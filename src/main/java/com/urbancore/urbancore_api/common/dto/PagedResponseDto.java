package com.urbancore.urbancore_api.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Generic paginated response wrapper")
public record PagedResponseDto<T>(
        @Schema(description = "List of items for the current page")
        List<T> content,

        @Schema(description = "Zero-based page index", example = "0")
        int page,

        @Schema(description = "Requested page size", example = "10")
        int size,

        @Schema(description = "Total number of elements across all pages", example = "125")
        long totalElements,

        @Schema(description = "Total number of pages", example = "13")
        int totalPages,

        @Schema(description = "Whether this is the first page", example = "true")
        boolean first,

        @Schema(description = "Whether this is the last page", example = "false")
        boolean last,

        @Schema(description = "Sort criteria applied to the query")
        List<SortDto> sort
) {
}
