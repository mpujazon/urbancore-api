package com.urbancore.urbancore_api.incident.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Image metadata linked to an incident")
public record IncidentImageDto(
        @Schema(description = "Unique image identifier", example = "img-01abc")
        String id,

        @Schema(description = "Full-size image URL (Cloudinary)", example = "https://res.cloudinary.com/urbancore/image/upload/v1/users/42/incident-uploads/photo.jpg")
        String url,

        @Schema(description = "Thumbnail URL (Cloudinary)", example = "https://res.cloudinary.com/urbancore/image/upload/c_thumb,w_200/v1/users/42/incident-uploads/photo.jpg")
        String thumbnailUrl,

        @Schema(description = "Cloudinary public identifier", example = "users/42/incident-uploads/photo")
        String publicId,

        @Schema(description = "MIME type of the image", example = "image/jpeg")
        String mimeType,

        @Schema(description = "File size in kilobytes", example = "1024")
        Integer sizeKb
) {
}
