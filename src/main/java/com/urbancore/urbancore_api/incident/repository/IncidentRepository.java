package com.urbancore.urbancore_api.incident.repository;

import com.urbancore.urbancore_api.incident.entity.Incident;
import com.urbancore.urbancore_api.incident.entity.IncidentCategory;
import com.urbancore.urbancore_api.incident.entity.IncidentStatus;
import com.urbancore.urbancore_api.incident.repository.projection.AreaCountProjection;
import com.urbancore.urbancore_api.incident.repository.projection.CategoryCountProjection;
import com.urbancore.urbancore_api.incident.repository.projection.DailyIncidentCountProjection;
import com.urbancore.urbancore_api.incident.repository.projection.IncidentPlannedActionsCountProjection;
import com.urbancore.urbancore_api.incident.repository.projection.StatusCountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, String>, JpaSpecificationExecutor<Incident> {
    List<Incident> findAllByOrderByCreatedAtDesc();
    List<Incident> findAllByReporterIdOrderByCreatedAtDesc(Long reporterId);

    @Query("""
            SELECT COUNT(i)
            FROM Incident i
            WHERE (:applyCity = false OR i.cityId = :cityId)
              AND (:applyFrom = false OR i.createdAt >= :fromDate)
              AND (:applyTo = false OR i.createdAt <= :toDate)
              AND (:applyCategory = false OR i.category = :category)
              AND (:applyStatus = false OR i.status = :status)
            """)
    long countByFilters(
            @Param("applyCity") boolean applyCity,
            @Param("cityId") String cityId,
            @Param("applyFrom") boolean applyFrom,
            @Param("fromDate") Instant fromDate,
            @Param("applyTo") boolean applyTo,
            @Param("toDate") Instant toDate,
            @Param("applyCategory") boolean applyCategory,
            @Param("category") IncidentCategory category,
            @Param("applyStatus") boolean applyStatus,
            @Param("status") IncidentStatus status
    );

    @Query("""
            SELECT COUNT(i)
            FROM Incident i
            WHERE (:applyCity = false OR i.cityId = :cityId)
              AND (:applyFrom = false OR i.createdAt >= :fromDate)
              AND (:applyTo = false OR i.createdAt <= :toDate)
              AND (:applyCategory = false OR i.category = :category)
              AND (:applyStatus = false OR i.status = :status)
              AND i.status NOT IN :closedStatuses
            """)
    long countOpenByFilters(
            @Param("applyCity") boolean applyCity,
            @Param("cityId") String cityId,
            @Param("applyFrom") boolean applyFrom,
            @Param("fromDate") Instant fromDate,
            @Param("applyTo") boolean applyTo,
            @Param("toDate") Instant toDate,
            @Param("applyCategory") boolean applyCategory,
            @Param("category") IncidentCategory category,
            @Param("applyStatus") boolean applyStatus,
            @Param("status") IncidentStatus status,
            @Param("closedStatuses") List<IncidentStatus> closedStatuses
    );

    @Query("""
            SELECT COUNT(i)
            FROM Incident i
            WHERE (:applyCity = false OR i.cityId = :cityId)
              AND (:applyFrom = false OR i.createdAt >= :fromDate)
              AND (:applyTo = false OR i.createdAt <= :toDate)
              AND (:applyCategory = false OR i.category = :category)
              AND (:applyStatus = false OR i.status = :status)
              AND i.status = com.urbancore.urbancore_api.incident.entity.IncidentStatus.RESOLVED
            """)
    long countResolvedByFilters(
            @Param("applyCity") boolean applyCity,
            @Param("cityId") String cityId,
            @Param("applyFrom") boolean applyFrom,
            @Param("fromDate") Instant fromDate,
            @Param("applyTo") boolean applyTo,
            @Param("toDate") Instant toDate,
            @Param("applyCategory") boolean applyCategory,
            @Param("category") IncidentCategory category,
            @Param("applyStatus") boolean applyStatus,
            @Param("status") IncidentStatus status
    );

    @Query("""
            SELECT COUNT(i)
            FROM Incident i
            WHERE (:applyCity = false OR i.cityId = :cityId)
              AND (:applyFrom = false OR i.createdAt >= :fromDate)
              AND (:applyTo = false OR i.createdAt <= :toDate)
              AND (:applyCategory = false OR i.category = :category)
              AND (:applyStatus = false OR i.status = :status)
              AND i.status = com.urbancore.urbancore_api.incident.entity.IncidentStatus.PLANNED
            """)
    long countPlannedByFilters(
            @Param("applyCity") boolean applyCity,
            @Param("cityId") String cityId,
            @Param("applyFrom") boolean applyFrom,
            @Param("fromDate") Instant fromDate,
            @Param("applyTo") boolean applyTo,
            @Param("toDate") Instant toDate,
            @Param("applyCategory") boolean applyCategory,
            @Param("category") IncidentCategory category,
            @Param("applyStatus") boolean applyStatus,
            @Param("status") IncidentStatus status
    );

    @Query("""
            SELECT i.status AS status, COUNT(i) AS count
            FROM Incident i
            WHERE (:applyCity = false OR i.cityId = :cityId)
              AND (:applyFrom = false OR i.createdAt >= :fromDate)
              AND (:applyTo = false OR i.createdAt <= :toDate)
              AND (:applyCategory = false OR i.category = :category)
              AND (:applyStatus = false OR i.status = :status)
            GROUP BY i.status
            """)
    List<StatusCountProjection> countByStatus(
            @Param("applyCity") boolean applyCity,
            @Param("cityId") String cityId,
            @Param("applyFrom") boolean applyFrom,
            @Param("fromDate") Instant fromDate,
            @Param("applyTo") boolean applyTo,
            @Param("toDate") Instant toDate,
            @Param("applyCategory") boolean applyCategory,
            @Param("category") IncidentCategory category,
            @Param("applyStatus") boolean applyStatus,
            @Param("status") IncidentStatus status
    );

    @Query("""
            SELECT i.category AS category, COUNT(i) AS count
            FROM Incident i
            WHERE (:applyCity = false OR i.cityId = :cityId)
              AND (:applyFrom = false OR i.createdAt >= :fromDate)
              AND (:applyTo = false OR i.createdAt <= :toDate)
              AND (:applyCategory = false OR i.category = :category)
              AND (:applyStatus = false OR i.status = :status)
            GROUP BY i.category
            """)
    List<CategoryCountProjection> countByCategory(
            @Param("applyCity") boolean applyCity,
            @Param("cityId") String cityId,
            @Param("applyFrom") boolean applyFrom,
            @Param("fromDate") Instant fromDate,
            @Param("applyTo") boolean applyTo,
            @Param("toDate") Instant toDate,
            @Param("applyCategory") boolean applyCategory,
            @Param("category") IncidentCategory category,
            @Param("applyStatus") boolean applyStatus,
            @Param("status") IncidentStatus status
    );

    @Query("""
            SELECT CAST(i.createdAt AS date) AS dateBucket, COUNT(i) AS count
            FROM Incident i
            WHERE (:applyCity = false OR i.cityId = :cityId)
              AND (:applyFrom = false OR i.createdAt >= :fromDate)
              AND (:applyTo = false OR i.createdAt <= :toDate)
              AND (:applyCategory = false OR i.category = :category)
              AND (:applyStatus = false OR i.status = :status)
            GROUP BY CAST(i.createdAt AS date)
            ORDER BY CAST(i.createdAt AS date) ASC
            """)
    List<DailyIncidentCountProjection> countByDay(
            @Param("applyCity") boolean applyCity,
            @Param("cityId") String cityId,
            @Param("applyFrom") boolean applyFrom,
            @Param("fromDate") Instant fromDate,
            @Param("applyTo") boolean applyTo,
            @Param("toDate") Instant toDate,
            @Param("applyCategory") boolean applyCategory,
            @Param("category") IncidentCategory category,
            @Param("applyStatus") boolean applyStatus,
            @Param("status") IncidentStatus status
    );

    @Query("""
            SELECT COALESCE(i.city, 'Unknown') AS area, COUNT(i) AS count
            FROM Incident i
            WHERE (:applyCity = false OR i.cityId = :cityId)
              AND (:applyFrom = false OR i.createdAt >= :fromDate)
              AND (:applyTo = false OR i.createdAt <= :toDate)
              AND (:applyCategory = false OR i.category = :category)
              AND (:applyStatus = false OR i.status = :status)
            GROUP BY COALESCE(i.city, 'Unknown')
            ORDER BY COUNT(i) DESC
            """)
    List<AreaCountProjection> countByArea(
            @Param("applyCity") boolean applyCity,
            @Param("cityId") String cityId,
            @Param("applyFrom") boolean applyFrom,
            @Param("fromDate") Instant fromDate,
            @Param("applyTo") boolean applyTo,
            @Param("toDate") Instant toDate,
            @Param("applyCategory") boolean applyCategory,
            @Param("category") IncidentCategory category,
            @Param("applyStatus") boolean applyStatus,
            @Param("status") IncidentStatus status
    );

    @Query("""
            SELECT AVG((EXTRACT(EPOCH FROM i.updatedAt) - EXTRACT(EPOCH FROM i.createdAt)) / 86400.0)
            FROM Incident i
            WHERE (:applyCity = false OR i.cityId = :cityId)
              AND (:applyFrom = false OR i.createdAt >= :fromDate)
              AND (:applyTo = false OR i.createdAt <= :toDate)
              AND (:applyCategory = false OR i.category = :category)
              AND (:applyStatus = false OR i.status = :status)
              AND i.status = com.urbancore.urbancore_api.incident.entity.IncidentStatus.RESOLVED
            """)
    Double averageResolutionDays(
            @Param("applyCity") boolean applyCity,
            @Param("cityId") String cityId,
            @Param("applyFrom") boolean applyFrom,
            @Param("fromDate") Instant fromDate,
            @Param("applyTo") boolean applyTo,
            @Param("toDate") Instant toDate,
            @Param("applyCategory") boolean applyCategory,
            @Param("category") IncidentCategory category,
            @Param("applyStatus") boolean applyStatus,
            @Param("status") IncidentStatus status
    );

    @Query("""
            SELECT i.id AS incidentId, COUNT(pa.id) AS plannedActionsCount
            FROM Incident i
            LEFT JOIN i.plannedActions pa
            WHERE i.id IN :incidentIds
            GROUP BY i.id
            """)
    List<IncidentPlannedActionsCountProjection> countPlannedActionsByIncidentIds(
            @Param("incidentIds") Collection<String> incidentIds
    );
}
