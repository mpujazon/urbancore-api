package com.urbancore.urbancore_api.ai.mapper;

import com.urbancore.urbancore_api.ai.dto.IncidentSuggestionResponse;
import com.urbancore.urbancore_api.models.IncidentCategory;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class IncidentSuggestionNormalizer {

    private static final int MAX_TITLE_LENGTH = 120;
    private static final int MAX_DESCRIPTION_LENGTH = 500;
    private static final String DEFAULT_TITLE = "Possible urban incident";
    private static final String DEFAULT_DESCRIPTION = "I noticed a possible urban incident that may require municipal review.";

    public IncidentSuggestionResponse normalize(String title, String description, String category) {
        String normalizedTitle = normalizeText(title, DEFAULT_TITLE, MAX_TITLE_LENGTH);
        return new IncidentSuggestionResponse(
                normalizedTitle,
                normalizeText(description, fallbackDescription(normalizedTitle), MAX_DESCRIPTION_LENGTH),
                normalizeCategory(category)
        );
    }

    private String fallbackDescription(String title) {
        if (title == null || title.isBlank() || DEFAULT_TITLE.equals(title)) {
            return DEFAULT_DESCRIPTION;
        }
        return "I noticed " + title.toLowerCase(Locale.ROOT) + " that may require municipal review.";
    }

    public String removeMarkdownFences(String raw) {
        if (raw == null) {
            return null;
        }

        String sanitized = raw.trim();
        if (!sanitized.startsWith("```")) {
            return sanitized;
        }

        int firstBreak = sanitized.indexOf('\n');
        if (firstBreak > -1) {
            sanitized = sanitized.substring(firstBreak + 1);
        }
        if (sanitized.endsWith("```")) {
            sanitized = sanitized.substring(0, sanitized.length() - 3);
        }
        return sanitized.trim();
    }

    private String normalizeText(String input, String fallback, int maxLength) {
        if (input == null || input.isBlank()) {
            return fallback;
        }
        String value = removeMarkdownFences(input).trim();
        if (value.isBlank()) {
            return fallback;
        }
        return value.length() > maxLength ? value.substring(0, maxLength) : value;
    }

    private IncidentCategory normalizeCategory(String input) {
        if (input == null || input.isBlank()) {
            return IncidentCategory.OTHER;
        }

        try {
            return IncidentCategory.valueOf(input.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return IncidentCategory.OTHER;
        }
    }
}
