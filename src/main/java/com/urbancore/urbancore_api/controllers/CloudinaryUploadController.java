package com.urbancore.urbancore_api.controllers;

import com.cloudinary.Cloudinary;
import com.urbancore.urbancore_api.dtos.UploadSignatureResponse;
import com.urbancore.urbancore_api.models.User;
import com.urbancore.urbancore_api.services.CurrentUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/uploads")
public class CloudinaryUploadController {

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
    public UploadSignatureResponse createUploadSignature(@AuthenticationPrincipal Jwt jwt) {
        User currentUser = currentUserService.getCurrentUser(jwt);

        Long timestamp = Instant.now().getEpochSecond();
        String folder = "users/" + currentUser.getId() + "/incident-uploads";

        Map<String, Object> paramsToSign = new HashMap<>();
        paramsToSign.put("timestamp", timestamp);
        paramsToSign.put("folder", folder);

        String signature = cloudinary.apiSignRequest(
                paramsToSign,
                cloudinary.config.apiSecret
        );

        return new UploadSignatureResponse(
                cloudName,
                apiKey,
                timestamp,
                folder,
                signature
        );
    }
}