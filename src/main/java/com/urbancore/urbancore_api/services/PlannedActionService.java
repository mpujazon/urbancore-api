package com.urbancore.urbancore_api.services;

import com.urbancore.urbancore_api.dtos.CreatePlannedActionRequest;
import com.urbancore.urbancore_api.dtos.PlannedActionResponse;
import com.urbancore.urbancore_api.dtos.UpdatePlannedActionRequest;
import com.urbancore.urbancore_api.mappers.PlannedActionMapper;
import com.urbancore.urbancore_api.models.Incident;
import com.urbancore.urbancore_api.models.IncidentStatus;
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
import java.util.List;
import java.util.UUID;

@Service
public class PlannedActionService {

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

        incident.setStatus(IncidentStatus.PLANNED);
        incidentRepository.save(incident);

        return plannedActionMapper.toResponse(saved);
    }

    @Transactional
    public PlannedActionResponse update(UUID plannedActionId, UpdatePlannedActionRequest request) {
        PlannedAction plannedAction = plannedActionRepository.findById(plannedActionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Planned action not found"));

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
        recalculateIncidentStatusIfNoActiveActions(saved.getIncident());

        return plannedActionMapper.toResponse(saved);
    }

    @Transactional
    public void delete(UUID plannedActionId) {
        PlannedAction plannedAction = plannedActionRepository.findById(plannedActionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Planned action not found"));

        Incident incident = plannedAction.getIncident();
        plannedActionRepository.delete(plannedAction);

        recalculateIncidentStatusIfNoActiveActions(incident);
    }

    @Transactional(readOnly = true)
    public List<PlannedActionResponse> findByIncident(String incidentId) {
        return plannedActionRepository.findByIncidentId(incidentId).stream()
                .map(plannedActionMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PlannedActionResponse> findByCityAndDateRange(String cityId, Instant from, Instant to) {
        if (from == null || to == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "from and to are required");
        }
        if (!to.isAfter(from)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "to must be after from");
        }

        return plannedActionRepository.findByIncidentCityIdAndScheduledStartBetween(cityId, from, to).stream()
                .map(plannedActionMapper::toResponse)
                .toList();
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

    private void recalculateIncidentStatusIfNoActiveActions(Incident incident) {
        boolean hasActiveActions = plannedActionRepository.existsByIncidentIdAndStatusNot(
                incident.getId(),
                PlannedActionStatus.CANCELLED
        );
        if (!hasActiveActions) {
            incident.setStatus(IncidentStatus.CANCELLED);
            incidentRepository.save(incident);
        }
    }
}
