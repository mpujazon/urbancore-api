package com.urbancore.urbancore_api.plannedaction.service;

import com.urbancore.urbancore_api.plannedaction.dto.CreatePlannedActionRequest;
import com.urbancore.urbancore_api.plannedaction.dto.UpdatePlannedActionRequest;
import com.urbancore.urbancore_api.plannedaction.mapper.PlannedActionMapper;
import com.urbancore.urbancore_api.incident.entity.Incident;
import com.urbancore.urbancore_api.incident.entity.IncidentStatus;
import com.urbancore.urbancore_api.plannedaction.entity.PlannedAction;
import com.urbancore.urbancore_api.plannedaction.entity.PlannedActionStatus;
import com.urbancore.urbancore_api.auth.entity.User;
import com.urbancore.urbancore_api.auth.entity.UserRole;
import com.urbancore.urbancore_api.incident.repository.IncidentRepository;
import com.urbancore.urbancore_api.plannedaction.repository.PlannedActionRepository;
import com.urbancore.urbancore_api.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlannedActionServiceTest {

    @Mock
    private PlannedActionRepository plannedActionRepository;

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlannedActionMapper plannedActionMapper;

    private PlannedActionService plannedActionService;

    @BeforeEach
    void setUp() {
        plannedActionService = new PlannedActionService(
                plannedActionRepository,
                incidentRepository,
                userRepository,
                plannedActionMapper
        );
    }

    @Test
    @DisplayName("should reject create when incident city differs from admin city")
    void rejectsCreateOutsideAssignedCity() {
        User admin = admin("city-admin");
        Incident incident = incident("other-city");
        when(incidentRepository.findById("inc-001")).thenReturn(Optional.of(incident));

        CreatePlannedActionRequest request = new CreatePlannedActionRequest(
                "inc-001",
                "Replace damaged lamp",
                "desc",
                Instant.parse("2026-06-10T08:00:00Z"),
                Instant.parse("2026-06-10T10:00:00Z"),
                null
        );

        assertThatThrownBy(() -> plannedActionService.create(request, admin))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Admin cannot access incidents outside assigned city");
    }

    @Test
    @DisplayName("should reject update when admin has no assigned city")
    void rejectsUpdateWithoutAssignedCity() {
        User admin = admin(null);
        Incident incident = incident("city-admin");
        User creator = admin("city-admin");
        PlannedAction action = new PlannedAction(
                incident,
                "Replace damaged lamp",
                "desc",
                Instant.parse("2026-06-10T08:00:00Z"),
                Instant.parse("2026-06-10T10:00:00Z"),
                null,
                creator
        );
        action.changeStatus(PlannedActionStatus.PLANNED);

        UUID plannedActionId = UUID.randomUUID();
        when(plannedActionRepository.findById(plannedActionId)).thenReturn(Optional.of(action));

        UpdatePlannedActionRequest request = new UpdatePlannedActionRequest(
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertThatThrownBy(() -> plannedActionService.update(plannedActionId, request, admin))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Admin user has no assigned cityId");
    }

    private User admin(String cityId) {
        User admin = new User();
        admin.setId(10L);
        admin.setRole(UserRole.ROLE_ADMIN);
        admin.setCityId(cityId);
        return admin;
    }

    private Incident incident(String cityId) {
        Incident incident = new Incident();
        incident.setId("inc-001");
        incident.setCityId(cityId);
        incident.setStatus(IncidentStatus.NEW);
        return incident;
    }
}
