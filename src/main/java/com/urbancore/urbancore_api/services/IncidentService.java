package com.urbancore.urbancore_api.services;

import com.urbancore.urbancore_api.dtos.*;
import com.urbancore.urbancore_api.models.*;
import com.urbancore.urbancore_api.repositories.IncidentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final CurrentUserService currentUserService;

    public IncidentService(IncidentRepository incidentRepository, CurrentUserService currentUserService) {
        this.incidentRepository = incidentRepository;
        this.currentUserService = currentUserService;
    }

    public IncidentDto createIncident(CreateIncidentRequest request, Jwt jwt) {
        validateRequest(request);

        User reporter = currentUserService.getCurrentUser(jwt);

        Incident incident = new Incident();
        incident.setTitle(request.title().trim());
        incident.setDescription(request.description().trim());
        incident.setCategory(request.category());
        incident.setStatus(IncidentStatus.NEW);
        incident.setPriority(request.priority() != null ? request.priority() : IncidentPriority.UNDEFINED);
        incident.setCityId(request.cityId());
        incident.setReporter(reporter);

        incident.setLat(request.location().lat());
        incident.setLng(request.location().lng());
        incident.setAddressLabel(request.location().addressLabel());
        incident.setArea(request.location().area());
        incident.setCity(request.location().city());
        incident.setGeohash(resolveGeohash(request.location()));

        List<IncidentImage> images = new ArrayList<>();
        for (IncidentImageDto imageDto : request.images()) {
            IncidentImage image = new IncidentImage();
            image.setId(imageDto.id() != null ? imageDto.id() : UUID.randomUUID().toString());
            image.setUrl(imageDto.url());
            image.setThumbnailUrl(imageDto.thumbnailUrl());
            image.setPublicId(imageDto.publicId());
            image.setMimeType(imageDto.mimeType());
            image.setSizeKb(imageDto.sizeKb());
            image.setIncident(incident);
            images.add(image);
        }
        incident.setImages(images);

        IncidentStatusHistory initialHistory = new IncidentStatusHistory();
        initialHistory.setId(UUID.randomUUID().toString());
        initialHistory.setFromStatus(IncidentStatus.NEW);
        initialHistory.setToStatus(IncidentStatus.NEW);
        initialHistory.setChangedBy(String.valueOf(reporter.getId()));
        initialHistory.setChangedAt(Instant.now());
        initialHistory.setIncident(incident);
        incident.setStatusHistory(new ArrayList<>(List.of(initialHistory)));

        Incident savedIncident = incidentRepository.save(incident);
        return toDto(savedIncident);
    }

    private void validateRequest(CreateIncidentRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
        }

        if (request.title() == null || request.title().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title is required");
        }

        if (request.description() == null || request.description().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "description is required");
        }

        if (request.category() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "category is required");
        }

        if (request.location() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "location is required");
        }

        if (request.images() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "images is required");
        }

        boolean hasInvalidImage = request.images().stream().anyMatch(i ->
                i == null || i.url() == null || i.url().isBlank() || i.publicId() == null || i.publicId().isBlank());
        if (hasInvalidImage) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "images[].url and images[].publicId are required");
        }
    }

    private String resolveGeohash(IncidentLocationDto location) {
        if (location.geohash() != null && !location.geohash().isBlank()) {
            return location.geohash();
        }

        return location.lat() + "," + location.lng();
    }

    private IncidentDto toDto(Incident incident) {
        IncidentReporterDto reporterDto = null;
        if (incident.getReporter() != null) {
            String email = Objects.toString(incident.getReporter().getEmail(), "");
            reporterDto = new IncidentReporterDto(
                    String.valueOf(incident.getReporter().getId()),
                    email,
                    incident.getReporter().getRole()
            );
        }

        IncidentLocationDto locationDto = new IncidentLocationDto(
                incident.getLat(),
                incident.getLng(),
                incident.getAddressLabel(),
                incident.getArea(),
                incident.getCity(),
                incident.getGeohash()
        );

        List<IncidentImageDto> imageDtos = incident.getImages().stream()
                .map(i -> new IncidentImageDto(
                        i.getId(),
                        i.getUrl(),
                        i.getThumbnailUrl(),
                        i.getPublicId(),
                        i.getMimeType(),
                        i.getSizeKb()
                ))
                .toList();

        List<IncidentStatusHistoryDto> statusHistoryDtos = incident.getStatusHistory().stream()
                .map(h -> new IncidentStatusHistoryDto(
                        h.getId(),
                        h.getFromStatus(),
                        h.getToStatus(),
                        h.getChangedBy(),
                        h.getReason(),
                        h.getChangedAt().toString()
                ))
                .toList();

        return new IncidentDto(
                incident.getId(),
                incident.getTitle(),
                incident.getDescription(),
                incident.getCategory(),
                incident.getStatus(),
                incident.getPriority(),
                incident.getCityId(),
                reporterDto,
                locationDto,
                imageDtos,
                List.of(),
                statusHistoryDtos,
                incident.getCreatedAt().toString(),
                incident.getUpdatedAt().toString()
        );
    }
}
