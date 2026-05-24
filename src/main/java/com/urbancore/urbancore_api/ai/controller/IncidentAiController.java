package com.urbancore.urbancore_api.ai.controller;

import com.urbancore.urbancore_api.ai.dto.IncidentSuggestionResponse;
import com.urbancore.urbancore_api.ai.service.IncidentImageSuggestionService;
import com.urbancore.urbancore_api.dtos.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI Suggestions", description = "AI-assisted incident form suggestions")
public class IncidentAiController {

    private final IncidentImageSuggestionService incidentImageSuggestionService;

    public IncidentAiController(IncidentImageSuggestionService incidentImageSuggestionService) {
        this.incidentImageSuggestionService = incidentImageSuggestionService;
    }

    @PostMapping(
            value = "/incident-suggestions",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Suggest incident fields from image",
            description = """
                    Analyzes an uploaded image and returns AI suggestions for incident form fields.
                    Requires authenticated user with ROLE_CITIZEN.
                    This endpoint does not create incidents, does not upload to Cloudinary,
                    and does not persist image or AI response.
                    """,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Suggestions generated successfully",
                    content = @Content(schema = @Schema(implementation = IncidentSuggestionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid image payload",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid Firebase JWT",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Authenticated user does not have the CITIZEN role",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "502", description = "AI suggestion response could not be processed",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "503", description = "AI provider unavailable",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public IncidentSuggestionResponse suggestIncidentFromImage(
            @RequestPart(name = "image", required = false) MultipartFile image
    ) {
        return incidentImageSuggestionService.suggestFromImage(image);
    }
}
