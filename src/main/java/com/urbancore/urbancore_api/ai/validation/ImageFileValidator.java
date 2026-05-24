package com.urbancore.urbancore_api.ai.validation;

import com.urbancore.urbancore_api.ai.exception.AiSuggestionException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Component
public class ImageFileValidator {

    private static final long MAX_IMAGE_SIZE_BYTES = 5L * 1024L * 1024L;
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/heic",
            "image/heif"
    );

    public void validate(MultipartFile image) {
        if (image == null) {
            throw new AiSuggestionException(HttpStatus.BAD_REQUEST, "AI_IMAGE_REQUIRED", "Image file is required.");
        }

        if (image.isEmpty()) {
            throw new AiSuggestionException(HttpStatus.BAD_REQUEST, "AI_INVALID_IMAGE", "Image file cannot be empty.");
        }

        if (image.getSize() > MAX_IMAGE_SIZE_BYTES) {
            throw new AiSuggestionException(HttpStatus.BAD_REQUEST, "AI_IMAGE_TOO_LARGE", "Image exceeds maximum size of 5MB.");
        }

        String contentType = image.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new AiSuggestionException(
                    HttpStatus.BAD_REQUEST,
                    "AI_UNSUPPORTED_IMAGE_TYPE",
                    "Only JPEG, PNG, WEBP and HEIC/HEIF images are supported."
            );
        }
    }
}
