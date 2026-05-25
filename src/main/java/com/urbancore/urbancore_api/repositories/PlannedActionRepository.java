package com.urbancore.urbancore_api.repositories;

import com.urbancore.urbancore_api.models.PlannedAction;
import com.urbancore.urbancore_api.models.PlannedActionStatus;
import com.urbancore.urbancore_api.models.IncidentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PlannedActionRepository extends JpaRepository<PlannedAction, UUID> {
    List<PlannedAction> findByIncidentId(String incidentId);

    List<PlannedAction> findByIncidentCityIdAndScheduledStartBetween(String cityId, Instant from, Instant to);

    @Query("""
            select pa
            from PlannedAction pa
            join fetch pa.incident i
            where pa.scheduledStart < :dateTo
              and (pa.scheduledEnd is null or pa.scheduledEnd > :dateFrom)
              and (:applyCity = false or i.cityId = :cityId)
              and (:status is null or pa.status = :status)
              and i.status <> :excludedIncidentStatus
            order by pa.scheduledStart asc
            """)
    List<PlannedAction> findPublicCalendarActions(
            @Param("applyCity") boolean applyCity,
            @Param("cityId") String cityId,
            @Param("dateFrom") Instant dateFrom,
            @Param("dateTo") Instant dateTo,
            @Param("status") PlannedActionStatus status,
            @Param("excludedIncidentStatus") IncidentStatus excludedIncidentStatus
    );

    boolean existsByIncidentIdAndStatusNot(String incidentId, PlannedActionStatus status);

    boolean existsByIncidentIdAndTitle(String incidentId, String title);
}
