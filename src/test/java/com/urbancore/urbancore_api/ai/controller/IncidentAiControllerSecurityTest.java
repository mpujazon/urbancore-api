package com.urbancore.urbancore_api.ai.controller;

import com.urbancore.urbancore_api.ai.dto.IncidentSuggestionResponse;
import com.urbancore.urbancore_api.ai.service.IncidentImageSuggestionService;
import com.urbancore.urbancore_api.incident.entity.IncidentCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IncidentAiControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IncidentImageSuggestionService incidentImageSuggestionService;

    @Test
    @DisplayName("request without token returns 401")
    void requestWithoutTokenReturns401() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "x".getBytes());

        mockMvc.perform(multipart("/api/ai/incident-suggestions").file(image))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    @DisplayName("authenticated user without CITIZEN role returns 403")
    void authenticatedUserWithoutCitizenRoleReturns403() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "x".getBytes());

        mockMvc.perform(multipart("/api/ai/incident-suggestions")
                        .file(image)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

    @Test
    @DisplayName("CITIZEN user can access endpoint")
    void citizenCanAccessEndpoint() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "x".getBytes());

        when(incidentImageSuggestionService.suggestFromImage(any())).thenReturn(
                new IncidentSuggestionResponse(
                        "Farola rota en la acera",
                        "Se observa una farola danada o apagada en una zona peatonal.",
                        IncidentCategory.LIGHTING
                )
        );

        mockMvc.perform(multipart("/api/ai/incident-suggestions")
                        .file(image)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CITIZEN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Farola rota en la acera"))
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.category").value("LIGHTING"));
    }
}
