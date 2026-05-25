package com.urbancore.urbancore_api.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbancore.urbancore_api.ai.dto.IncidentSuggestionResponse;
import com.urbancore.urbancore_api.ai.exception.AiSuggestionException;
import com.urbancore.urbancore_api.ai.mapper.IncidentSuggestionNormalizer;
import com.urbancore.urbancore_api.ai.prompt.IncidentSuggestionPromptFactory;
import com.urbancore.urbancore_api.ai.validation.ImageFileValidator;
import com.urbancore.urbancore_api.incident.entity.IncidentCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IncidentImageSuggestionServiceTest {

    private ChatModel chatModel;
    private IncidentImageSuggestionService service;

    @BeforeEach
    void setUp() {
        chatModel = mock(ChatModel.class);
        service = new IncidentImageSuggestionService(
                chatModel,
                new ObjectMapper(),
                new IncidentSuggestionPromptFactory(),
                new ImageFileValidator(),
                new IncidentSuggestionNormalizer()
        );
    }

    @Test
    @DisplayName("request without image returns AI_IMAGE_REQUIRED")
    void requestWithoutImageReturnsRequiredError() {
        AiSuggestionException ex = assertThrows(AiSuggestionException.class, () -> service.suggestFromImage(null));
        assertEquals("AI_IMAGE_REQUIRED", ex.getCode());
    }

    @Test
    @DisplayName("empty file returns AI_INVALID_IMAGE")
    void emptyFileReturnsInvalidImageError() {
        MockMultipartFile image = new MockMultipartFile("image", "a.jpg", "image/jpeg", new byte[0]);
        AiSuggestionException ex = assertThrows(AiSuggestionException.class, () -> service.suggestFromImage(image));
        assertEquals("AI_INVALID_IMAGE", ex.getCode());
    }

    @Test
    @DisplayName("file larger than 5MB returns AI_IMAGE_TOO_LARGE")
    void tooLargeFileReturnsError() {
        byte[] payload = new byte[5 * 1024 * 1024 + 1];
        MockMultipartFile image = new MockMultipartFile("image", "a.jpg", "image/jpeg", payload);
        AiSuggestionException ex = assertThrows(AiSuggestionException.class, () -> service.suggestFromImage(image));
        assertEquals("AI_IMAGE_TOO_LARGE", ex.getCode());
    }

    @Test
    @DisplayName("unsupported MIME type returns AI_UNSUPPORTED_IMAGE_TYPE")
    void unsupportedMimeTypeReturnsError() {
        MockMultipartFile image = new MockMultipartFile("image", "a.gif", "image/gif", "x".getBytes());
        AiSuggestionException ex = assertThrows(AiSuggestionException.class, () -> service.suggestFromImage(image));
        assertEquals("AI_UNSUPPORTED_IMAGE_TYPE", ex.getCode());
    }

    @Test
    @DisplayName("image/png is allowed")
    void pngImageIsAllowed() {
        MockMultipartFile image = new MockMultipartFile("image", "a.png", "image/png", "x".getBytes());
        ChatResponse response = mock(ChatResponse.class, RETURNS_DEEP_STUBS);
        when(response.getResult().getOutput().getText()).thenReturn("{\"title\":\"a\",\"description\":\"b\",\"category\":\"OTHER\"}");
        when(chatModel.call(any(Prompt.class))).thenReturn(response);

        IncidentSuggestionResponse result = service.suggestFromImage(image);
        assertEquals(IncidentCategory.OTHER, result.category());
    }

    @Test
    @DisplayName("image/webp is allowed")
    void webpImageIsAllowed() {
        MockMultipartFile image = new MockMultipartFile("image", "a.webp", "image/webp", "x".getBytes());
        ChatResponse response = mock(ChatResponse.class, RETURNS_DEEP_STUBS);
        when(response.getResult().getOutput().getText()).thenReturn("{\"title\":\"a\",\"description\":\"b\",\"category\":\"OTHER\"}");
        when(chatModel.call(any(Prompt.class))).thenReturn(response);

        IncidentSuggestionResponse result = service.suggestFromImage(image);
        assertEquals(IncidentCategory.OTHER, result.category());
    }

    @Test
    @DisplayName("image/heic is allowed")
    void heicImageIsAllowed() {
        MockMultipartFile image = new MockMultipartFile("image", "a.heic", "image/heic", "x".getBytes());
        ChatResponse response = mock(ChatResponse.class, RETURNS_DEEP_STUBS);
        when(response.getResult().getOutput().getText()).thenReturn("{\"title\":\"a\",\"description\":\"b\",\"category\":\"OTHER\"}");
        when(chatModel.call(any(Prompt.class))).thenReturn(response);

        IncidentSuggestionResponse result = service.suggestFromImage(image);
        assertEquals(IncidentCategory.OTHER, result.category());
    }

    @Test
    @DisplayName("valid AI response is mapped correctly")
    void validAiResponseIsMapped() {
        MockMultipartFile image = new MockMultipartFile("image", "a.jpg", "image/jpeg", "x".getBytes());
        ChatResponse response = mock(ChatResponse.class, RETURNS_DEEP_STUBS);
        when(response.getResult().getOutput().getText()).thenReturn("{" +
                "\"title\":\"Farola rota en la acera\"," +
                "\"description\":\"Se observa una farola danada o apagada en una zona peatonal.\"," +
                "\"category\":\"LIGHTING\"}");
        when(chatModel.call(any(Prompt.class))).thenReturn(response);

        IncidentSuggestionResponse result = service.suggestFromImage(image);

        assertEquals("Farola rota en la acera", result.title());
        assertEquals(IncidentCategory.LIGHTING, result.category());
    }

    @Test
    @DisplayName("invalid or null category is normalized to OTHER")
    void invalidCategoryIsNormalizedToOther() {
        MockMultipartFile image = new MockMultipartFile("image", "a.jpg", "image/jpeg", "x".getBytes());
        ChatResponse response = mock(ChatResponse.class, RETURNS_DEEP_STUBS);
        when(response.getResult().getOutput().getText()).thenReturn("{" +
                "\"title\":\"Titulo\"," +
                "\"description\":\"Descripcion\"," +
                "\"category\":\"NOT_A_CATEGORY\"}");
        when(chatModel.call(any(Prompt.class))).thenReturn(response);

        IncidentSuggestionResponse result = service.suggestFromImage(image);
        assertEquals(IncidentCategory.OTHER, result.category());
    }

    @Test
    @DisplayName("blank title and description use safe fallbacks")
    void blankTitleAndDescriptionUseFallbacks() {
        MockMultipartFile image = new MockMultipartFile("image", "a.jpg", "image/jpeg", "x".getBytes());
        ChatResponse response = mock(ChatResponse.class, RETURNS_DEEP_STUBS);
        when(response.getResult().getOutput().getText()).thenReturn("{" +
                "\"title\":\"\"," +
                "\"description\":\"\"," +
                "\"category\":null}");
        when(chatModel.call(any(Prompt.class))).thenReturn(response);

        IncidentSuggestionResponse result = service.suggestFromImage(image);
        assertEquals("Possible urban incident", result.title());
        assertEquals("I noticed a possible urban incident that may require municipal review.", result.description());
        assertEquals(IncidentCategory.OTHER, result.category());
    }

    @Test
    @DisplayName("markdown fenced JSON is parsed correctly")
    void markdownFenceIsParsedCorrectly() {
        MockMultipartFile image = new MockMultipartFile("image", "a.jpg", "image/jpeg", "x".getBytes());
        ChatResponse response = mock(ChatResponse.class, RETURNS_DEEP_STUBS);
        when(response.getResult().getOutput().getText()).thenReturn("```json\n{\"title\":\"t\",\"description\":\"d\",\"category\":\"LIGHTING\"}\n```");
        when(chatModel.call(any(Prompt.class))).thenReturn(response);

        IncidentSuggestionResponse result = service.suggestFromImage(image);
        assertEquals(IncidentCategory.LIGHTING, result.category());
    }

    @Test
    @DisplayName("response with extra text around JSON is parsed")
    void responseWithExtraTextAroundJsonIsParsed() {
        MockMultipartFile image = new MockMultipartFile("image", "a.jpg", "image/jpeg", "x".getBytes());
        ChatResponse response = mock(ChatResponse.class, RETURNS_DEEP_STUBS);
        when(response.getResult().getOutput().getText()).thenReturn("Suggestion:\n{\"title\":\"t\",\"description\":\"d\",\"category\":\"LIGHTING\"}\nDone");
        when(chatModel.call(any(Prompt.class))).thenReturn(response);

        IncidentSuggestionResponse result = service.suggestFromImage(image);
        assertEquals(IncidentCategory.LIGHTING, result.category());
    }

    @Test
    @DisplayName("truncated provider JSON uses recoverable fields and fallbacks")
    void truncatedProviderJsonUsesRecoverableFieldsAndFallbacks() {
        MockMultipartFile image = new MockMultipartFile("image", "a.jpg", "image/jpeg", "x".getBytes());
        ChatResponse response = mock(ChatResponse.class, RETURNS_DEEP_STUBS);
        when(response.getResult().getOutput().getText()).thenReturn("{\"title\": \"Uneven Sid");
        when(chatModel.call(any(Prompt.class))).thenReturn(response);

        IncidentSuggestionResponse result = service.suggestFromImage(image);
        assertEquals("Uneven Sid", result.title());
        assertEquals("I noticed uneven sid that may require municipal review.", result.description());
        assertEquals(IncidentCategory.OTHER, result.category());
    }

    @Test
    @DisplayName("nested provider JSON is parsed")
    void nestedProviderJsonIsParsed() {
        MockMultipartFile image = new MockMultipartFile("image", "a.jpg", "image/jpeg", "x".getBytes());
        ChatResponse response = mock(ChatResponse.class, RETURNS_DEEP_STUBS);
        when(response.getResult().getOutput().getText()).thenReturn("{\"suggestion\":{\"title\":\"t\",\"description\":\"d\",\"category\":\"LIGHTING\"}}");
        when(chatModel.call(any(Prompt.class))).thenReturn(response);

        IncidentSuggestionResponse result = service.suggestFromImage(image);
        assertEquals(IncidentCategory.LIGHTING, result.category());
    }

    @Test
    @DisplayName("lenient provider JSON is parsed")
    void lenientProviderJsonIsParsed() {
        MockMultipartFile image = new MockMultipartFile("image", "a.jpg", "image/jpeg", "x".getBytes());
        ChatResponse response = mock(ChatResponse.class, RETURNS_DEEP_STUBS);
        when(response.getResult().getOutput().getText()).thenReturn("{title:'t', description:'d', category:'LIGHTING',}");
        when(chatModel.call(any(Prompt.class))).thenReturn(response);

        IncidentSuggestionResponse result = service.suggestFromImage(image);
        assertEquals(IncidentCategory.LIGHTING, result.category());
    }

    @Test
    @DisplayName("non-JSON provider response returns AI_INVALID_PROVIDER_RESPONSE")
    void nonJsonResponseReturnsControlledError() {
        MockMultipartFile image = new MockMultipartFile("image", "a.jpg", "image/jpeg", "x".getBytes());
        ChatResponse response = mock(ChatResponse.class, RETURNS_DEEP_STUBS);
        when(response.getResult().getOutput().getText()).thenReturn("not-json");
        when(chatModel.call(any(Prompt.class))).thenReturn(response);

        AiSuggestionException ex = assertThrows(AiSuggestionException.class, () -> service.suggestFromImage(image));
        assertEquals("AI_INVALID_PROVIDER_RESPONSE", ex.getCode());
    }

    @Test
    @DisplayName("AI provider failure returns AI_PROVIDER_UNAVAILABLE")
    void providerFailureReturnsControlledError() {
        MockMultipartFile image = new MockMultipartFile("image", "a.jpg", "image/jpeg", "x".getBytes());
        when(chatModel.call(any(Prompt.class))).thenThrow(new RuntimeException("provider down"));

        AiSuggestionException ex = assertThrows(AiSuggestionException.class, () -> service.suggestFromImage(image));
        assertEquals("AI_PROVIDER_UNAVAILABLE", ex.getCode());
    }
}
