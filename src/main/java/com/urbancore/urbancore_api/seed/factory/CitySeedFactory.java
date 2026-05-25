package com.urbancore.urbancore_api.seed.factory;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CitySeedFactory {

    public List<CitySeed> createCities() {
        return List.of(
                new CitySeed("barcelona", "Barcelona", "es-barcelona"),
                new CitySeed("lhospitalet", "L'Hospitalet de Llobregat", "es-l-hospitalet-de-llobregat"),
                new CitySeed("santa-coloma", "Santa Coloma de Gramenet", "es-santa-coloma-de-gramenet"),
                new CitySeed("terrassa", "Terrassa", "es-terrassa")
        );
    }

    public record CitySeed(String key, String name, String slug) {
    }
}
