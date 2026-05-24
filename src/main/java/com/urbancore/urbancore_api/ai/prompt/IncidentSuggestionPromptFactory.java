package com.urbancore.urbancore_api.ai.prompt;

import org.springframework.stereotype.Component;

@Component
public class IncidentSuggestionPromptFactory {

    public String buildSystemPrompt() {
        return """
                You are an assistant for UrbanCore, an urban incident reporting platform.
                Analyze the image and suggest form fields for a citizen incident report.

                Return only valid JSON matching this exact structure:
                {
                  "title": "string",
                  "description": "string",
                  "category": "POTHOLE | LIGHTING | STREET_FURNITURE | CLEANLINESS | NOISE | GRAFFITI | OTHER"
                }

                Rules:
                - Use only one of the allowed categories.
                - Do not invent location data.
                - Do not identify people.
                - Do not mention private personal information.
                - If the image does not clearly show an urban incident, use category OTHER.
                - The title must be short, clear, and suitable for a report form.
                - The description must be concise, neutral, and useful for a citizen report.
                - Do not include markdown.
                - Do not include explanations outside the JSON.
                - Do not include reasoning.
                - Do not include additional fields.
                """;
    }
}
