package com.urbancore.urbancore_api.seed;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbancore.urbancore_api.incident.entity.IncidentCategory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class SeedImageCatalog {

    private static final String CATALOG_PATH = "seed/cloudinary-seed-images.json";

    private final Map<IncidentCategory, List<SeedImageAsset>> catalog;

    public SeedImageCatalog(ObjectMapper objectMapper) {
        this.catalog = loadCatalog(objectMapper);
        validateCompleteness(this.catalog);
    }

    public SeedImageAsset pickImage(IncidentCategory category, Random random) {
        List<SeedImageAsset> assets = catalog.get(category);
        if (assets == null || assets.isEmpty()) {
            throw new IllegalStateException("Seed image catalog has no assets for category: " + category);
        }
        return assets.get(random.nextInt(assets.size()));
    }

    private Map<IncidentCategory, List<SeedImageAsset>> loadCatalog(ObjectMapper objectMapper) {
        ClassPathResource resource = new ClassPathResource(CATALOG_PATH);
        try (InputStream inputStream = resource.getInputStream()) {
            TypeReference<Map<IncidentCategory, List<SeedImageAsset>>> type = new TypeReference<>() {
            };
            return objectMapper.readValue(inputStream, type);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load seed image catalog from " + CATALOG_PATH, e);
        }
    }

    private void validateCompleteness(Map<IncidentCategory, List<SeedImageAsset>> loadedCatalog) {
        for (IncidentCategory category : IncidentCategory.values()) {
            List<SeedImageAsset> assets = loadedCatalog.get(category);
            if (assets == null || assets.isEmpty()) {
                throw new IllegalStateException("Category " + category + " has no available assets in " + CATALOG_PATH);
            }
            for (SeedImageAsset asset : assets) {
                if (isBlank(asset.publicId()) || isBlank(asset.url()) || isBlank(asset.thumbnailUrl()) || isBlank(asset.alt())) {
                    throw new IllegalStateException("Invalid image asset for category " + category + ": all fields must be non-blank");
                }
            }
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public record SeedImageAsset(String publicId, String url, String thumbnailUrl, String alt) {
    }
}
