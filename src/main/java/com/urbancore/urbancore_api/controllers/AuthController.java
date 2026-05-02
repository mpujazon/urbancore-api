package com.urbancore.urbancore_api.controllers;

import com.urbancore.urbancore_api.models.User;
import com.urbancore.urbancore_api.services.AuthService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/sync")
    public User syncUser(@AuthenticationPrincipal Jwt jwt) {
        return authService.syncUser(jwt);
    }
}