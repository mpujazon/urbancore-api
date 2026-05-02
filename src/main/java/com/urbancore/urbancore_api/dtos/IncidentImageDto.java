package com.urbancore.urbancore_api.dtos;

public record IncidentImageDto(
        String id,
        String url,
        String thumbnailUrl,
        String publicId,
        String mimeType,
        Integer sizeKb
) {
}
