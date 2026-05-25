package com.urbancore.urbancore_api.ai.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IncidentAiControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatModel chatModel;

    @Test
    @DisplayName("request without image returns 400")
    void requestWithoutImageReturns400() throws Exception {
        mockMvc.perform(multipart("/api/ai/incident-suggestions")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CITIZEN"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("AI_IMAGE_REQUIRED"));
    }

    @Test
    @DisplayName("empty image returns 400")
    void emptyImageReturns400() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "empty.jpg", "image/jpeg", new byte[0]);

        mockMvc.perform(multipart("/api/ai/incident-suggestions")
                        .file(image)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CITIZEN"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("AI_INVALID_IMAGE"));
    }

    @Test
    @DisplayName("image bigger than 5MB returns 400")
    void imageTooLargeReturns400() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "big.jpg",
                "image/jpeg",
                new byte[5 * 1024 * 1024 + 1]
        );

        mockMvc.perform(multipart("/api/ai/incident-suggestions")
                        .file(image)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CITIZEN"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("AI_IMAGE_TOO_LARGE"));
    }

    @Test
    @DisplayName("unsupported MIME type returns 400")
    void unsupportedMimeTypeReturns400() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "a.pdf", "application/pdf", "x".getBytes());

        mockMvc.perform(multipart("/api/ai/incident-suggestions")
                        .file(image)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CITIZEN"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("AI_UNSUPPORTED_IMAGE_TYPE"));
    }

    @Test
    @DisplayName("jpeg, png, webp and heic images are accepted")
    void allowedMimeTypesAreAccepted() throws Exception {
        ChatResponse response = mock(ChatResponse.class, RETURNS_DEEP_STUBS);
        when(response.getResult().getOutput().getText()).thenReturn("{\"title\":\"t\",\"description\":\"d\",\"category\":\"OTHER\"}");
        when(chatModel.call(any(Prompt.class))).thenReturn(response);

        MockMultipartFile jpeg = new MockMultipartFile("image", "a.jpg", "image/jpeg", "x".getBytes());
        MockMultipartFile png = new MockMultipartFile("image", "a.png", "image/png", "x".getBytes());
        MockMultipartFile webp = new MockMultipartFile("image", "a.webp", "image/webp", "x".getBytes());
        MockMultipartFile heic = new MockMultipartFile("image", "a.heic", "image/heic", "x".getBytes());

        mockMvc.perform(multipart("/api/ai/incident-suggestions").file(jpeg)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CITIZEN"))))
                .andExpect(status().isOk());

        mockMvc.perform(multipart("/api/ai/incident-suggestions").file(png)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CITIZEN"))))
                .andExpect(status().isOk());

        mockMvc.perform(multipart("/api/ai/incident-suggestions").file(webp)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CITIZEN"))))
                .andExpect(status().isOk());

        mockMvc.perform(multipart("/api/ai/incident-suggestions").file(heic)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CITIZEN"))))
                .andExpect(status().isOk());
    }
}
