package com.urbancore.urbancore_api.upload.controller;

import com.cloudinary.Cloudinary;
import com.urbancore.urbancore_api.common.dto.ApiErrorResponse;
import com.urbancore.urbancore_api.upload.dto.UploadSignatureResponse;
import com.urbancore.urbancore_api.auth.entity.User;
import com.urbancore.urbancore_api.auth.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/uploads")
@Tag(name = "Uploads", description = "Cloudinary upload signature generation")
public class CloudinaryUploadController {

    private static final String UPLOAD_FORMAT = "webp";

    private final Cloudinary cloudinary;
    private final CurrentUserService currentUserService;

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    public CloudinaryUploadController(
            Cloudinary cloudinary,
            CurrentUserService currentUserService
    ) {
        this.cloudinary = cloudinary;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/signature")
    @Operation(
            summary = "Generate a Cloudinary upload signature",
            description = """
                    Creates a signed upload signature so the Angular frontend can upload \
                    images directly to Cloudinary before creating an incident. \
                    The folder is scoped to the authenticated user and output format is forced to WebP. \
                    Requires CITIZEN role.
                    """,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Upload signature generated",
                    content = @Content(schema = @Schema(implementation = UploadSignatureResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing or invalid Firebase JWT",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Authenticated user does not have the CITIZEN role",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public UploadSignatureResponse createUploadSignature(@AuthenticationPrincipal Jwt jwt) {
        User currentUser = currentUserService.getCurrentUser(jwt);

        Long timestamp = Instant.now().getEpochSecond();
        String folder = "users/" + currentUser.getId() + "/incident-uploads";

        Map<String, Object> paramsToSign = new HashMap<>();
        paramsToSign.put("timestamp", timestamp);
        paramsToSign.put("folder", folder);
        paramsToSign.put("format", UPLOAD_FORMAT);

        String signature = cloudinary.apiSignRequest(
                paramsToSign,
                cloudinary.config.apiSecret
        );

        return new UploadSignatureResponse(
                cloudName,
                apiKey,
                timestamp,
                folder,
                UPLOAD_FORMAT,
                signature
        );
    }
}
