package com.urbancore.urbancore_api.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {
    
    @GetMapping("/me")
    public Map<String, Object> getMyInfo(@AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> userInfo = new HashMap<>();

        userInfo.put("mensaje", "¡Bienvenido! Has atravesado la seguridad de Spring Boot");
        userInfo.put("firebase_uid", jwt.getSubject());
        userInfo.put("email", jwt.getClaimAsString("email"));
        userInfo.put("email_verificado", jwt.getClaimAsBoolean("email_verified"));

        return userInfo;
    }
}