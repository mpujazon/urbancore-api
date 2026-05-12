package com.urbancore.urbancore_api.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Public-safe image metadata attached to an incident")
public record PublicIncidentImageResponse(
        @Schema(description = "Unique image identifier", example = "img-01abc")
        String id,

        @Schema(description = "Full-size image URL", example = "https://res.cloudinary.com/urbancore/image/upload/v1/public/incidents/photo.jpg")
        String url,

        @Schema(description = "Thumbnail image URL", example = "https://res.cloudinary.com/urbancore/image/upload/c_thumb,w_300/v1/public/incidents/photo.jpg")
        String thumbnailUrl,

        @Schema(description = "MIME type of the image", example = "image/jpeg")
        String mimeType,

        @Schema(description = "File size in kilobytes", example = "1024")
        Integer sizeKb
) {
}
