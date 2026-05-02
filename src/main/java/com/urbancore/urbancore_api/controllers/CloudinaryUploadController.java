package com.urbancore.urbancore_api.controllers;

import com.cloudinary.Cloudinary;
import com.urbancore.urbancore_api.dtos.UploadSignatureResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/uploads")
public class CloudinaryUploadController {
    private final Cloudinary cloudinary;

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    public CloudinaryUploadController(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @PostMapping("/signature")
    public UploadSignatureResponse createUploadSignature(@AuthenticationPrincipal Jwt jwt) {
        Long timestamp = Instant.now().getEpochSecond();

        String firebaseUid = jwt.getSubject();
        String folder = "incidents/" + firebaseUid;

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