package com.urbancore.urbancore_api.services;

import com.urbancore.urbancore_api.dtos.AreaCountResponse;
import com.urbancore.urbancore_api.dtos.CategoryCountResponse;
import com.urbancore.urbancore_api.dtos.DailyIncidentCountResponse;
import com.urbancore.urbancore_api.dtos.IncidentStatsFilter;
import com.urbancore.urbancore_api.dtos.IncidentStatsSummaryResponse;
import com.urbancore.urbancore_api.dtos.StatusCountResponse;
import com.urbancore.urbancore_api.models.IncidentCategory;
import com.urbancore.urbancore_api.models.IncidentStatus;
import com.urbancore.urbancore_api.repositories.IncidentRepository;
import com.urbancore.urbancore_api.repositories.projections.DailyIncidentCountProjection;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
public class IncidentStatsService {

    private static final List<IncidentStatus> CLOSED_STATUSES = List.of(
            IncidentStatus.RESOLVED,
            IncidentStatus.REJECTED,
            IncidentStatus.CANCELLED
    );
    private static final Instant MIN_INSTANT = Instant.parse("1970-01-01T00:00:00Z");
    private static final Instant MAX_INSTANT = Instant.parse("9999-12-31T23:59:59Z");

    private final IncidentRepository incidentRepository;

    public IncidentStatsService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    public IncidentStatsSummaryResponse getIncidentSummary(IncidentStatsFilter filter) {
        validateFilter(filter);

        String cityId = filter.normalizedCityId();
        boolean applyCity = cityId != null;
        boolean applyFrom = filter.from() != null;
        boolean applyTo = filter.to() != null;
        boolean applyCategory = filter.category() != null;
        boolean applyStatus = filter.status() != null;

        String effectiveCityId = applyCity ? cityId : "";
        Instant effectiveFrom = applyFrom ? filter.from() : MIN_INSTANT;
        Instant effectiveTo = applyTo ? filter.to() : MAX_INSTANT;
        IncidentCategory effectiveCategory = applyCategory ? filter.category() : IncidentCategory.OTHER;
        IncidentStatus effectiveStatus = applyStatus ? filter.status() : IncidentStatus.NEW;

        long totalIncidents = incidentRepository.countByFilters(
                applyCity,
                effectiveCityId,
                applyFrom,
                effectiveFrom,
                applyTo,
                effectiveTo,
                applyCategory,
                effectiveCategory,
                applyStatus,
                effectiveStatus
        );

        long openIncidents = incidentRepository.countOpenByFilters(
                applyCity,
                effectiveCityId,
                applyFrom,
                effectiveFrom,
                applyTo,
                effectiveTo,
                applyCategory,
                effectiveCategory,
                applyStatus,
                effectiveStatus,
                CLOSED_STATUSES
        );

        long resolvedIncidents = incidentRepository.countResolvedByFilters(
                applyCity,
                effectiveCityId,
                applyFrom,
                effectiveFrom,
                applyTo,
                effectiveTo,
                applyCategory,
                effectiveCategory,
                applyStatus,
                effectiveStatus
        );

        long plannedIncidents = incidentRepository.countPlannedByFilters(
                applyCity,
                effectiveCityId,
                applyFrom,
                effectiveFrom,
                applyTo,
                effectiveTo,
                applyCategory,
                effectiveCategory,
                applyStatus,
                effectiveStatus
        );

        Double averageResolutionDays = roundToSingleDecimal(
                incidentRepository.averageResolutionDays(
                        applyCity,
                        effectiveCityId,
                        applyFrom,
                        effectiveFrom,
                        applyTo,
                        effectiveTo,
                        applyCategory,
                        effectiveCategory,
                        applyStatus,
                        effectiveStatus
                )
        );

        List<StatusCountResponse> byStatus = incidentRepository.countByStatus(
                        applyCity,
                        effectiveCityId,
                        applyFrom,
                        effectiveFrom,
                        applyTo,
                        effectiveTo,
                        applyCategory,
                        effectiveCategory,
                        applyStatus,
                        effectiveStatus
                ).stream()
                .map(item -> new StatusCountResponse(item.getStatus(), item.getCount()))
                .toList();

        List<CategoryCountResponse> byCategory = incidentRepository.countByCategory(
                        applyCity,
                        effectiveCityId,
                        applyFrom,
                        effectiveFrom,
                        applyTo,
                        effectiveTo,
                        applyCategory,
                        effectiveCategory,
                        applyStatus,
                        effectiveStatus
                ).stream()
                .map(item -> new CategoryCountResponse(item.getCategory(), item.getCount()))
                .toList();

        List<DailyIncidentCountResponse> trend = incidentRepository.countByDay(
                        applyCity,
                        effectiveCityId,
                        applyFrom,
                        effectiveFrom,
                        applyTo,
                        effectiveTo,
                        applyCategory,
                        effectiveCategory,
                        applyStatus,
                        effectiveStatus
                ).stream()
                .map(this::mapDailyCount)
                .toList();

        List<AreaCountResponse> byArea = incidentRepository.countByArea(
                        applyCity,
                        effectiveCityId,
                        applyFrom,
                        effectiveFrom,
                        applyTo,
                        effectiveTo,
                        applyCategory,
                        effectiveCategory,
                        applyStatus,
                        effectiveStatus
                ).stream()
                .map(item -> new AreaCountResponse(item.getArea(), item.getCount()))
                .toList();

        return new IncidentStatsSummaryResponse(
                totalIncidents,
                openIncidents,
                resolvedIncidents,
                plannedIncidents,
                averageResolutionDays,
                byStatus,
                byCategory,
                trend,
                byArea
        );
    }

    private void validateFilter(IncidentStatsFilter filter) {
        if (filter.from() != null && filter.to() != null && filter.from().isAfter(filter.to())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "from must be before or equal to to");
        }
    }

    private DailyIncidentCountResponse mapDailyCount(DailyIncidentCountProjection row) {
        Object rawDate = row.getDateBucket();
        LocalDate date;

        if (rawDate instanceof LocalDate localDate) {
            date = localDate;
        } else {
            date = LocalDate.parse(String.valueOf(rawDate));
        }

        return new DailyIncidentCountResponse(date, row.getCount());
    }

    private Double roundToSingleDecimal(Double value) {
        if (value == null) {
            return null;
        }

        return BigDecimal.valueOf(value)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
