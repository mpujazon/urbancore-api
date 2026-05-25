package com.urbancore.urbancore_api.upload.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Parameters required to sign an upload request for Cloudinary")
public record UploadSignatureResponse(
        @Schema(description = "Cloudinary cloud name", example = "urbancore")
        String cloudName,

        @Schema(description = "Cloudinary API key", example = "123456789012345")
        String apiKey,

        @Schema(description = "Unix timestamp (seconds) used to sign the request", example = "1744627200")
        Long timestamp,

        @Schema(description = "Cloudinary folder where the file will be stored", example = "users/42/incident-uploads")
        String folder,

        @Schema(description = "Output image format enforced for signed uploads", example = "webp")
        String format,

        @Schema(description = "HMAC-SHA1 signature for the upload parameters", example = "a1b2c3d4e5f6...")
        String signature
) {
}
