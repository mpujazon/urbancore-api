package com.urbancore.urbancore_api.plannedaction.mapper;

import com.urbancore.urbancore_api.plannedaction.dto.PlannedActionResponse;
import com.urbancore.urbancore_api.plannedaction.dto.PublicPlannedActionCalendarItemResponse;
import com.urbancore.urbancore_api.plannedaction.dto.PublicPlannedActionIncidentRefResponse;
import com.urbancore.urbancore_api.plannedaction.entity.PlannedAction;
import com.urbancore.urbancore_api.plannedaction.dto.PublicPlannedActionResponse;
import org.springframework.stereotype.Component;

@Component
public class PlannedActionMapper {
    public PlannedActionResponse toResponse(PlannedAction action) {
        return new PlannedActionResponse(
                action.getId(),
                action.getIncident().getId(),
                action.getTitle(),
                action.getDescription(),
                action.getStatus(),
                action.getScheduledStart(),
                action.getScheduledEnd(),
                action.getAssignedToUser() != null ? action.getAssignedToUser().getId() : null,
                action.getCreatedBy().getId(),
                action.getCreatedAt(),
                action.getUpdatedAt()
        );
    }

    public PublicPlannedActionResponse toPublicResponse(PlannedAction action) {
        return new PublicPlannedActionResponse(
                action.getId(),
                action.getIncident().getId(),
                action.getTitle(),
                action.getDescription(),
                action.getStatus(),
                action.getScheduledStart(),
                action.getScheduledEnd(),
                action.getAssignedToUser() != null ? action.getAssignedToUser().getId() : null,
                action.getCreatedBy().getId(),
                action.getCreatedAt(),
                action.getUpdatedAt()
        );
    }

    public PublicPlannedActionCalendarItemResponse toPublicCalendarItemResponse(PlannedAction action) {
        return new PublicPlannedActionCalendarItemResponse(
                action.getId(),
                action.getTitle(),
                action.getDescription(),
                action.getStatus(),
                action.getScheduledStart(),
                action.getScheduledEnd(),
                new PublicPlannedActionIncidentRefResponse(
                        action.getIncident().getId(),
                        action.getIncident().getTitle(),
                        action.getIncident().getCategory(),
                        action.getIncident().getStatus(),
                        action.getIncident().getCityId(),
                        action.getIncident().getAddressLabel(),
                        action.getIncident().getLat(),
                        action.getIncident().getLng()
                )
        );
    }
}
