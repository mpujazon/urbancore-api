package com.urbancore.urbancore_api.services;

import com.urbancore.urbancore_api.dtos.CreatePlannedActionRequest;
import com.urbancore.urbancore_api.dtos.PlannedActionResponse;
import com.urbancore.urbancore_api.dtos.PublicPlannedActionCalendarItemResponse;
import com.urbancore.urbancore_api.dtos.UpdatePlannedActionRequest;
import com.urbancore.urbancore_api.mappers.PlannedActionMapper;
import com.urbancore.urbancore_api.models.Incident;
import com.urbancore.urbancore_api.models.IncidentStatus;
import com.urbancore.urbancore_api.models.IncidentStatusHistory;
import com.urbancore.urbancore_api.models.PlannedAction;
import com.urbancore.urbancore_api.models.PlannedActionStatus;
import com.urbancore.urbancore_api.models.User;
import com.urbancore.urbancore_api.repositories.IncidentRepository;
import com.urbancore.urbancore_api.repositories.PlannedActionRepository;
import com.urbancore.urbancore_api.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PlannedActionService {

    private static final long MAX_PUBLIC_CALENDAR_RANGE_DAYS = 366;

    private final PlannedActionRepository plannedActionRepository;
    private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;
    private final PlannedActionMapper plannedActionMapper;

    public PlannedActionService(
            PlannedActionRepository plannedActionRepository,
            IncidentRepository incidentRepository,
            UserRepository userRepository,
            PlannedActionMapper plannedActionMapper
    ) {
        this.plannedActionRepository = plannedActionRepository;
        this.incidentRepository = incidentRepository;
        this.userRepository = userRepository;
        this.plannedActionMapper = plannedActionMapper;
    }

    @Transactional
    public PlannedActionResponse create(CreatePlannedActionRequest request, Long currentUserId) {
        validateDateRange(request.scheduledStart(), request.scheduledEnd());

        Incident incident = incidentRepository.findById(request.incidentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Incident not found"));
        assertIncidentIsMutable(incident);

        User createdBy = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Creator user not found"));

        User assignedToUser = resolveAssignedUser(request.assignedToUserId());

        PlannedAction plannedAction = new PlannedAction(
                incident,
                request.title().trim(),
                request.description(),
                request.scheduledStart(),
                request.scheduledEnd(),
                assignedToUser,
                createdBy
        );

        PlannedAction saved = plannedActionRepository.save(plannedAction);
        transitionIncidentStatus(incident, IncidentStatus.PLANNED, String.valueOf(currentUserId));

        return plannedActionMapper.toResponse(saved);
    }

    @Transactional
    public PlannedActionResponse update(UUID plannedActionId, UpdatePlannedActionRequest request, Long currentUserId) {
        PlannedAction plannedAction = plannedActionRepository.findById(plannedActionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Planned action not found"));

        assertIncidentIsMutable(plannedAction.getIncident());

        Instant nextStart = request.scheduledStart() != null ? request.scheduledStart() : plannedAction.getScheduledStart();
        Instant nextEnd = request.scheduledEnd() != null ? request.scheduledEnd() : plannedAction.getScheduledEnd();
        validateDateRange(nextStart, nextEnd);

        String nextTitle = request.title() != null ? request.title().trim() : plannedAction.getTitle();
        String nextDescription = request.description() != null ? request.description() : plannedAction.getDescription();
        User nextAssignedUser = request.assignedToUserId() != null
                ? resolveAssignedUser(request.assignedToUserId())
                : plannedAction.getAssignedToUser();

        plannedAction.updateDetails(nextTitle, nextDescription, nextStart, nextEnd, nextAssignedUser);

        PlannedActionStatus nextStatus = request.status() != null ? request.status() : plannedAction.getStatus();
        if (nextStatus == PlannedActionStatus.CANCELLED) {
            plannedAction.cancel();
        } else {
            plannedAction.changeStatus(nextStatus);
        }

        PlannedAction saved = plannedActionRepository.save(plannedAction);
        recalculateIncidentStatusIfNoActiveActions(saved.getIncident(), String.valueOf(currentUserId));

        return plannedActionMapper.toResponse(saved);
    }

    @Transactional
    public void delete(UUID plannedActionId, Long currentUserId) {
        PlannedAction plannedAction = plannedActionRepository.findById(plannedActionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Planned action not found"));

        assertIncidentIsMutable(plannedAction.getIncident());

        Incident incident = plannedAction.getIncident();
        plannedActionRepository.delete(plannedAction);

        recalculateIncidentStatusIfNoActiveActions(incident, String.valueOf(currentUserId));
    }

    @Transactional(readOnly = true)
    public List<PlannedActionResponse> findByIncident(String incidentId) {
        return plannedActionRepository.findByIncidentId(incidentId).stream()
                .map(plannedActionMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PublicPlannedActionCalendarItemResponse> findPublicCalendarActions(
            String cityId,
            String dateFrom,
            String dateTo,
            PlannedActionStatus status
    ) {
        String normalizedCityId = normalizeCityId(cityId);
        Instant from = parseDateFrom(dateFrom);
        Instant to = parseDateTo(dateTo);
        validatePublicDateRange(from, to);

        return plannedActionRepository.findPublicCalendarActions(normalizedCityId != null, normalizedCityId, from, to, status, IncidentStatus.NULL).stream()
                .map(plannedActionMapper::toPublicCalendarItemResponse)
                .toList();
    }

    private String normalizeCityId(String cityId) {
        if (cityId == null || cityId.isBlank()) {
            return null;
        }
        return cityId.trim();
    }

    private User resolveAssignedUser(Long assignedToUserId) {
        if (assignedToUserId == null) {
            return null;
        }
        return userRepository.findById(assignedToUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assigned user not found"));
    }

    private void validateDateRange(Instant scheduledStart, Instant scheduledEnd) {
        if (scheduledStart == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "scheduledStart is required");
        }
        if (scheduledEnd != null && !scheduledEnd.isAfter(scheduledStart)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "scheduledEnd must be after scheduledStart");
        }
    }

    private void recalculateIncidentStatusIfNoActiveActions(Incident incident, String changedBy) {
        boolean hasActiveActions = plannedActionRepository.existsByIncidentIdAndStatusNot(
                incident.getId(),
                PlannedActionStatus.CANCELLED
        );
        if (!hasActiveActions) {
            transitionIncidentStatus(incident, IncidentStatus.CANCELLED, changedBy);
        }
    }

    private void transitionIncidentStatus(Incident incident, IncidentStatus nextStatus, String changedBy) {
        IncidentStatus currentStatus = incident.getStatus();
        if (currentStatus == nextStatus) {
            return;
        }

        incident.setStatus(nextStatus);

        List<IncidentStatusHistory> history = incident.getStatusHistory();
        if (history == null) {
            history = new ArrayList<>();
            incident.setStatusHistory(history);
        }

        IncidentStatusHistory historyEntry = new IncidentStatusHistory();
        historyEntry.setId(UUID.randomUUID().toString());
        historyEntry.setFromStatus(currentStatus);
        historyEntry.setToStatus(nextStatus);
        historyEntry.setChangedBy(changedBy);
        historyEntry.setChangedAt(Instant.now());
        historyEntry.setIncident(incident);
        history.add(historyEntry);

        incidentRepository.save(incident);
    }

    private void assertIncidentIsMutable(Incident incident) {
        IncidentStatus status = incident.getStatus();
        if (status == IncidentStatus.CANCELLED
                || status == IncidentStatus.REJECTED
                || status == IncidentStatus.RESOLVED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Incident cannot be modified when status is CANCELLED, REJECTED, or RESOLVED"
            );
        }
    }

    private Instant parseDateFrom(String rawDateFrom) {
        if (rawDateFrom == null || rawDateFrom.isBlank()) {
            throw badRequest("PLANNED_ACTION_INVALID_DATE_RANGE", "dateFrom is required");
        }

        return parseDateOrDateTime(rawDateFrom.trim(), false);
    }

    private Instant parseDateTo(String rawDateTo) {
        if (rawDateTo == null || rawDateTo.isBlank()) {
            throw badRequest("PLANNED_ACTION_INVALID_DATE_RANGE", "dateTo is required");
        }

        return parseDateOrDateTime(rawDateTo.trim(), true);
    }

    private Instant parseDateOrDateTime(String rawValue, boolean isDateTo) {
        try {
            return Instant.parse(rawValue);
        } catch (DateTimeParseException ignored) {
        }

        try {
            return OffsetDateTime.parse(rawValue).toInstant();
        } catch (DateTimeParseException ignored) {
        }

        try {
            LocalDate asDate = LocalDate.parse(rawValue);
            if (isDateTo) {
                return asDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
            }
            return asDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        } catch (DateTimeParseException ignored) {
        }

        throw badRequest("PLANNED_ACTION_INVALID_DATE_RANGE", "date parameters must be valid ISO-8601 values");
    }

    private void validatePublicDateRange(Instant dateFrom, Instant dateTo) {
        if (!dateTo.isAfter(dateFrom)) {
            throw badRequest("PLANNED_ACTION_INVALID_DATE_RANGE", "dateFrom must be before dateTo");
        }

        long days = java.time.Duration.between(dateFrom, dateTo).toDays();
        if (days > MAX_PUBLIC_CALENDAR_RANGE_DAYS) {
            throw badRequest("PLANNED_ACTION_DATE_RANGE_TOO_LARGE", "date range cannot exceed 12 months");
        }
    }

    private ResponseStatusException badRequest(String code, String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, code + ": " + message);
    }
}
