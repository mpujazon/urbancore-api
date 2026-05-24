package com.urbancore.urbancore_api.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.urbancore.urbancore_api.ai.dto.IncidentSuggestionResponse;
import com.urbancore.urbancore_api.ai.exception.AiSuggestionException;
import com.urbancore.urbancore_api.ai.mapper.IncidentSuggestionNormalizer;
import com.urbancore.urbancore_api.ai.prompt.IncidentSuggestionPromptFactory;
import com.urbancore.urbancore_api.ai.validation.ImageFileValidator;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class IncidentImageSuggestionService {

    private static final Logger log = LoggerFactory.getLogger(IncidentImageSuggestionService.class);
    private static final Pattern TITLE_PATTERN = Pattern.compile("['\"]?title['\"]?\\s*:\\s*['\"]([^'\"]*)(?:['\"]|$)", Pattern.CASE_INSENSITIVE);
    private static final Pattern DESCRIPTION_PATTERN = Pattern.compile("['\"]?description['\"]?\\s*:\\s*['\"]([^'\"]*)(?:['\"]|$)", Pattern.CASE_INSENSITIVE);
    private static final Pattern CATEGORY_PATTERN = Pattern.compile("['\"]?category['\"]?\\s*:\\s*['\"]?([A-Z_]+)['\"]?", Pattern.CASE_INSENSITIVE);

    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;
    private final ObjectMapper lenientObjectMapper;
    private final BeanOutputConverter<AiSuggestionPayload> outputConverter;
    private final IncidentSuggestionPromptFactory promptFactory;
    private final ImageFileValidator imageFileValidator;
    private final IncidentSuggestionNormalizer normalizer;

    public IncidentImageSuggestionService(
            ChatModel chatModel,
            ObjectMapper objectMapper,
            IncidentSuggestionPromptFactory promptFactory,
            ImageFileValidator imageFileValidator,
            IncidentSuggestionNormalizer normalizer
    ) {
        this.chatModel = chatModel;
        this.objectMapper = objectMapper;
        this.outputConverter = new BeanOutputConverter<>(AiSuggestionPayload.class, objectMapper);
        this.lenientObjectMapper = JsonMapper.builder()
                .enable(JsonReadFeature.ALLOW_SINGLE_QUOTES)
                .enable(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES)
                .enable(JsonReadFeature.ALLOW_TRAILING_COMMA)
                .build();
        this.promptFactory = promptFactory;
        this.imageFileValidator = imageFileValidator;
        this.normalizer = normalizer;
    }

    public IncidentSuggestionResponse suggestFromImage(MultipartFile image) {
        imageFileValidator.validate(image);

        try {
            UserMessage userMessage = UserMessage.builder()
                    .text(promptFactory.buildSystemPrompt() + "\n\n" + outputConverter.getFormat())
                    .media(toMedia(image))
                    .build();

            ChatResponse response = chatModel.call(new Prompt(List.of(userMessage)));
            String rawContent = response != null && response.getResult() != null && response.getResult().getOutput() != null
                    ? response.getResult().getOutput().getText()
                    : null;
            AiSuggestionPayload payload = parsePayload(rawContent);
            return normalizer.normalize(payload.title(), payload.description(), payload.category());
        } catch (AiSuggestionException ex) {
            throw ex;
        } catch (Exception ex) {
            throw mapProviderException(ex);
        }
    }

    private AiSuggestionException mapProviderException(Exception ex) {
        Throwable rootCause = rootCause(ex);
        log.warn(
                "AI provider call failed for incident suggestion endpoint: {} - {}",
                rootCause.getClass().getSimpleName(),
                rootCause.getMessage()
        );

        if (ex instanceof RestClientResponseException restEx) {
            HttpStatus status = HttpStatus.resolve(restEx.getStatusCode().value());
            if (status == HttpStatus.BAD_REQUEST || status == HttpStatus.UNSUPPORTED_MEDIA_TYPE) {
                return new AiSuggestionException(
                        HttpStatus.BAD_REQUEST,
                        "AI_UNSUPPORTED_IMAGE_TYPE",
                        "The AI provider rejected this image format. Try JPEG, PNG or WEBP."
                );
            }
            if (status == HttpStatus.TOO_MANY_REQUESTS) {
                return new AiSuggestionException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "AI_PROVIDER_UNAVAILABLE",
                        "AI provider quota or rate limit exceeded. Try again later."
                );
            }
        }

        if (rootCause instanceof ResourceAccessException) {
            return new AiSuggestionException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "AI_PROVIDER_UNAVAILABLE",
                    "AI provider request timed out. Try again later."
            );
        }

        String providerMessage = rootCause.getMessage() != null ? rootCause.getMessage().toLowerCase() : "";
        if (providerMessage.contains("high demand") || providerMessage.contains("try again later")) {
            return new AiSuggestionException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "AI_PROVIDER_UNAVAILABLE",
                    "Gemini is currently experiencing high demand. Try again later or use another Gemini model."
            );
        }

        return new AiSuggestionException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "AI_PROVIDER_UNAVAILABLE",
                "AI provider is currently unavailable."
        );
    }

    private Throwable rootCause(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current;
    }

    private Media toMedia(MultipartFile image) {
        try {
            MimeType mimeType = MimeType.valueOf(image.getContentType());
            ByteArrayResource resource = new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename() != null ? image.getOriginalFilename() : "incident-image";
                }
            };
            return new Media(mimeType, resource);
        } catch (IOException ex) {
            throw new AiSuggestionException(HttpStatus.BAD_REQUEST, "AI_INVALID_IMAGE", "Image file could not be read.");
        }
    }

    private AiSuggestionPayload parsePayload(String rawContent) {
        if (rawContent == null || rawContent.isBlank()) {
            throw new AiSuggestionException(HttpStatus.BAD_GATEWAY, "AI_SUGGESTION_FAILED", "AI suggestion response was empty.");
        }

        String sanitized = normalizer.removeMarkdownFences(rawContent);
        sanitized = extractJsonObject(sanitized);

        try {
            return outputConverter.convert(sanitized);
        } catch (RuntimeException ex) {
            // Fall through to defensive parsing for provider responses that are close to, but not exactly, schema-compliant JSON.
        }

        try {
            return objectMapper.readValue(sanitized.getBytes(StandardCharsets.UTF_8), AiSuggestionPayload.class);
        } catch (IOException ex) {
            AiSuggestionPayload treePayload = parseFromJsonTree(objectMapper, sanitized);
            if (treePayload != null) {
                return treePayload;
            }

            try {
                return lenientObjectMapper.readValue(sanitized.getBytes(StandardCharsets.UTF_8), AiSuggestionPayload.class);
            } catch (IOException ignored) {
                AiSuggestionPayload lenientTreePayload = parseFromJsonTree(lenientObjectMapper, sanitized);
                if (lenientTreePayload != null) {
                    return lenientTreePayload;
                }

                AiSuggestionPayload regexPayload = extractByRegex(sanitized);
                if (regexPayload != null) {
                    return regexPayload;
                }
                throw new AiSuggestionException(HttpStatus.BAD_GATEWAY, "AI_INVALID_PROVIDER_RESPONSE", "AI suggestion response could not be parsed.");
            }
        }
    }

    private AiSuggestionPayload parseFromJsonTree(ObjectMapper mapper, String text) {
        try {
            JsonNode root = mapper.readTree(text);
            String title = textValue(root.findValue("title"));
            String description = textValue(root.findValue("description"));
            String category = textValue(root.findValue("category"));
            if (title == null && description == null && category == null) {
                return null;
            }
            return new AiSuggestionPayload(title, description, category);
        } catch (IOException ex) {
            return null;
        }
    }

    private String textValue(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        return node.asText(null);
    }

    private String extractJsonObject(String text) {
        if (text == null) {
            return null;
        }

        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1).trim();
        }
        return text.trim();
    }

    private AiSuggestionPayload extractByRegex(String text) {
        Matcher titleMatcher = TITLE_PATTERN.matcher(text);
        Matcher descriptionMatcher = DESCRIPTION_PATTERN.matcher(text);
        Matcher categoryMatcher = CATEGORY_PATTERN.matcher(text);

        if (!titleMatcher.find() && !descriptionMatcher.find() && !categoryMatcher.find()) {
            return null;
        }

        String title = titleMatcher.reset().find() ? titleMatcher.group(1) : null;
        String description = descriptionMatcher.reset().find() ? descriptionMatcher.group(1) : null;
        String category = categoryMatcher.reset().find() ? categoryMatcher.group(1) : null;
        return new AiSuggestionPayload(title, description, category);
    }

    private record AiSuggestionPayload(
            String title,
            String description,
            String category
    ) {
    }
}
