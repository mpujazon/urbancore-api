package com.urbancore.urbancore_api.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("should expose supported cities for frontend integration")
    void returnsSupportedCities() throws Exception {
        assertCitiesResponse("/api/cities");
    }

    @Test
    @DisplayName("should keep legacy cities path available")
    void returnsSupportedCitiesFromLegacyPath() throws Exception {
        assertCitiesResponse("/cities");
    }

    private void assertCitiesResponse(String path) throws Exception {
        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$[0].id", matchesPattern("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")))
                .andExpect(jsonPath("$[0].name").value("Barcelona"))
                .andExpect(jsonPath("$[0].slug").value("es-barcelona"))
                .andExpect(jsonPath("$[0].center").doesNotExist())
                .andExpect(jsonPath("$[0].bounds").doesNotExist());
    }
}
