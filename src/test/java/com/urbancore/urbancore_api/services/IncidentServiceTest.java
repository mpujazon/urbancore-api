package com.urbancore.urbancore_api.services;

import com.urbancore.urbancore_api.dtos.CreateIncidentRequest;
import com.urbancore.urbancore_api.dtos.IncidentDto;
import com.urbancore.urbancore_api.dtos.IncidentLocationDto;
import com.urbancore.urbancore_api.mappers.PlannedActionMapper;
import com.urbancore.urbancore_api.mappers.PublicIncidentMapper;
import com.urbancore.urbancore_api.models.City;
import com.urbancore.urbancore_api.models.Incident;
import com.urbancore.urbancore_api.models.IncidentCategory;
import com.urbancore.urbancore_api.models.IncidentPriority;
import com.urbancore.urbancore_api.models.User;
import com.urbancore.urbancore_api.models.UserRole;
import com.urbancore.urbancore_api.repositories.CityRepository;
import com.urbancore.urbancore_api.repositories.IncidentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private PublicIncidentMapper publicIncidentMapper;

    @Mock
    private PlannedActionMapper plannedActionMapper;

    @Mock
    private Jwt jwt;

    private IncidentService incidentService;
    private User reporter;

    @BeforeEach
    void setUp() {
        incidentService = new IncidentService(
                incidentRepository,
                cityRepository,
                currentUserService,
                publicIncidentMapper,
                plannedActionMapper
        );

        reporter = new User();
        reporter.setId(42L);
        reporter.setEmail("citizen@example.com");
        reporter.setRole(UserRole.ROLE_CITIZEN);

    }

    @Test
    @DisplayName("should keep provided cityId and not resolve citySlug")
    void keepsProvidedCityId() {
        stubSuccessfulIncidentCreation();
        String cityId = UUID.randomUUID().toString();

        IncidentDto response = incidentService.createIncident(validRequest(cityId, "es-barcelona", "Barcelona"), jwt);

        assertThat(response.cityId()).isEqualTo(cityId);
        verifyNoInteractions(cityRepository);
    }

    @Test
    @DisplayName("should link incident to existing city by citySlug")
    void linksExistingCityBySlug() {
        stubSuccessfulIncidentCreation();
        City city = new City();
        city.setId(UUID.randomUUID());
        city.setName("Barcelona");
        city.setSlug("es-barcelona");
        when(cityRepository.findBySlug("es-barcelona")).thenReturn(Optional.of(city));

        IncidentDto response = incidentService.createIncident(validRequest(null, "es-barcelona", "Barcelona"), jwt);

        assertThat(response.cityId()).isEqualTo(city.getId().toString());
        verify(cityRepository, never()).save(any(City.class));
    }

    @Test
    @DisplayName("should create city when citySlug does not exist")
    void createsCityWhenSlugDoesNotExist() {
        stubSuccessfulIncidentCreation();
        UUID cityId = UUID.randomUUID();
        when(cityRepository.findBySlug("es-girona")).thenReturn(Optional.empty());
        when(cityRepository.save(any(City.class))).thenAnswer(invocation -> {
            City city = invocation.getArgument(0);
            city.setId(cityId);
            return city;
        });

        IncidentDto response = incidentService.createIncident(validRequest(null, "es-girona", "Girona"), jwt);

        assertThat(response.cityId()).isEqualTo(cityId.toString());
        verify(cityRepository).save(any(City.class));
    }

    @Test
    @DisplayName("should require cityId or citySlug")
    void requiresCityIdOrCitySlug() {
        assertThatThrownBy(() -> incidentService.createIncident(validRequest(null, null, "Barcelona"), jwt))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("cityId or citySlug is required");

        verify(incidentRepository, never()).save(any(Incident.class));
    }

    private CreateIncidentRequest validRequest(String cityId, String citySlug, String cityName) {
        return new CreateIncidentRequest(
                "Broken street light",
                "The street light has been off for three nights",
                IncidentCategory.LIGHTING,
                IncidentPriority.MEDIUM,
                cityId,
                citySlug,
                new IncidentLocationDto(41.3874, 2.1686, "Carrer de Balmes, 42", cityName, "sp3e1g"),
                List.of()
        );
    }

    private void stubSuccessfulIncidentCreation() {
        when(currentUserService.getCurrentUser(jwt)).thenReturn(reporter);
        when(incidentRepository.save(any(Incident.class))).thenAnswer(invocation -> {
            Incident incident = invocation.getArgument(0);
            incident.onCreate();
            return incident;
        });
    }
}
