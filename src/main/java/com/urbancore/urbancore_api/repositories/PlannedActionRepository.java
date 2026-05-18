package com.urbancore.urbancore_api.repositories;

import com.urbancore.urbancore_api.models.Incident;
import com.urbancore.urbancore_api.models.PlannedActionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import com.urbancore.plannedactions.domain.PlannedAction;

public interface PlannedActionRepository extends JpaRepository<PlannedAction, UUID> {
    List<PlannedAction> findByIncident(UUID incident);
    List<PlannedAction> findByIncidentCityIdAndScheduledStartBetween(UUID cityId, Instant from, Instant to);
    
    boolean existsByIncidentIdAndStatusNot(UUID incidentId, PlannedActionStatus status);
}
