package com.urbancore.urbancore_api.repositories;

import com.urbancore.urbancore_api.models.PlannedAction;
import com.urbancore.urbancore_api.models.PlannedActionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PlannedActionRepository extends JpaRepository<PlannedAction, UUID> {
    List<PlannedAction> findByIncidentId(String incidentId);

    List<PlannedAction> findByIncidentCityIdAndScheduledStartBetween(String cityId, Instant from, Instant to);

    boolean existsByIncidentIdAndStatusNot(String incidentId, PlannedActionStatus status);
}
