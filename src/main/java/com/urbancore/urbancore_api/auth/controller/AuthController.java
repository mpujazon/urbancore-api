package com.urbancore.urbancore_api.auth.controller;

import com.urbancore.urbancore_api.common.dto.ApiErrorResponse;
import com.urbancore.urbancore_api.auth.entity.User;
import com.urbancore.urbancore_api.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Firebase-based authentication and user synchronization")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/sync")
    @Operation(
            summary = "Sync Firebase user with backend",
            description = """
                    Synchronizes the Firebase-authenticated user with the UrbanCore database. \
                    If the user does not yet exist in the backend, a new record is created \
                    with the default CITIZEN role. If the user already exists, the existing \
                    record is returned unchanged. Called by the frontend after Firebase login.
                    """,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User synchronized successfully",
                    content = @Content(schema = @Schema(implementation = User.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing or invalid Firebase JWT",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public User syncUser(@AuthenticationPrincipal Jwt jwt) {
        return authService.syncUser(jwt);
    }
}
