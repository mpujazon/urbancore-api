package com.urbancore.urbancore_api.dtos;

public record UploadSignatureResponse(
        String cloudName,
        String apiKey,
        Long timestamp,
        String folder,
        String signature
) {}