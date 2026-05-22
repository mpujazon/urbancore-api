package com.urbancore.urbancore_api.config;

import com.urbancore.urbancore_api.models.City;
import com.urbancore.urbancore_api.repositories.CityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class CityDataSeeder {

    private static final UUID BARCELONA_ID = UUID.fromString("2f3c7a4e-9d2b-4f16-a51d-9d4b2f6e0c12");
    private static final String BARCELONA_SLUG = "es-barcelona";

    @Bean
    CommandLineRunner seedCities(CityRepository cityRepository) {
        return args -> {
            if (cityRepository.existsBySlug(BARCELONA_SLUG)) {
                return;
            }

            City barcelona = new City();
            barcelona.setId(BARCELONA_ID);
            barcelona.setName("Barcelona");
            barcelona.setSlug(BARCELONA_SLUG);
            cityRepository.save(barcelona);
        };
    }
}
