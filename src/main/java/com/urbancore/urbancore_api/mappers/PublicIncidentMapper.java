package com.urbancore.urbancore_api.mappers;

import com.urbancore.urbancore_api.dtos.PublicIncidentDetailResponse;
import com.urbancore.urbancore_api.dtos.PublicIncidentImageResponse;
import com.urbancore.urbancore_api.dtos.PublicIncidentLocationResponse;
import com.urbancore.urbancore_api.dtos.PublicIncidentStatusHistoryResponse;
import com.urbancore.urbancore_api.models.Incident;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PublicIncidentMapper {

    public PublicIncidentDetailResponse toDetailResponse(Incident incident) {
        PublicIncidentLocationResponse location = new PublicIncidentLocationResponse(
                incident.getLat(),
                incident.getLng(),
                incident.getAddressLabel(),
                incident.getArea(),
                incident.getCity()
        );

        List<PublicIncidentImageResponse> images = incident.getImages().stream()
                .map(image -> new PublicIncidentImageResponse(
                        image.getId(),
                        image.getUrl(),
                        image.getThumbnailUrl(),
                        image.getMimeType(),
                        image.getSizeKb()
                ))
                .toList();

        List<PublicIncidentStatusHistoryResponse> statusHistory = incident.getStatusHistory().stream()
                .map(history -> new PublicIncidentStatusHistoryResponse(
                        history.getId(),
                        history.getFromStatus(),
                        history.getToStatus(),
                        history.getChangedAt()
                ))
                .toList();

        return new PublicIncidentDetailResponse(
                incident.getId(),
                incident.getTitle(),
                incident.getDescription(),
                incident.getCategory(),
                incident.getStatus(),
                incident.getPriority(),
                incident.getCityId(),
                location,
                images,
                List.of(),
                statusHistory,
                incident.getCreatedAt(),
                incident.getUpdatedAt()
        );
    }
}
