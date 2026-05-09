package com.urbancore.urbancore_api.services;

import com.urbancore.urbancore_api.dtos.*;
import com.urbancore.urbancore_api.models.*;
import com.urbancore.urbancore_api.repositories.IncidentRepository;
import com.urbancore.urbancore_api.repositories.IncidentSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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

    public List<IncidentListItemDto> getCurrentCitizenIncidents(Jwt jwt) {
        User currentUser = currentUserService.getCurrentUser(jwt);

        return incidentRepository.findAllByReporterIdOrderByCreatedAtDesc(currentUser.getId()).stream()
                .map(this::toListItemDto)
                .toList();
    }

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt", "updatedAt", "status", "priority", "category", "title"
    );

    private static final int MAX_PAGE_SIZE = 50;

    public PagedResponseDto<IncidentListItemDto> getAllIncidents(IncidentFilterDto filters, int page, int size, String sortParam) {
        if (page < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "page must not be negative");
        }
        if (size < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "size must be greater than 0");
        }

        int effectiveSize = Math.min(size, MAX_PAGE_SIZE);
        Sort sort = parseSort(sortParam);
        Pageable pageable = PageRequest.of(page, effectiveSize, sort);

        Specification<Incident> spec = IncidentSpecification.withFilters(filters);
        Page<Incident> resultPage = incidentRepository.findAll(spec, pageable);

        List<IncidentListItemDto> content = resultPage.getContent().stream()
                .map(this::toListItemDto)
                .toList();

        List<SortDto> sortDtos = sort.stream()
                .map(o -> new SortDto(o.getProperty(), o.getDirection().name()))
                .toList();

        return new PagedResponseDto<>(
                content,
                resultPage.getNumber(),
                resultPage.getSize(),
                resultPage.getTotalElements(),
                resultPage.getTotalPages(),
                resultPage.isFirst(),
                resultPage.isLast(),
                sortDtos
        );
    }

    private Sort parseSort(String sortParam) {
        if (sortParam == null || sortParam.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }

        String[] parts = sortParam.split(",");
        if (parts.length == 0) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }

        String field = parts[0].trim();
        String rawDirection = parts.length > 1 ? parts[1].trim().toUpperCase() : "DESC";

        if (!ALLOWED_SORT_FIELDS.contains(field)) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }

        Sort.Direction direction = "ASC".equals(rawDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, field);
    }

    private IncidentListItemDto toListItemDto(Incident incident) {
        String thumbnailUrl = null;
        if (incident.getImages() != null && !incident.getImages().isEmpty()) {
            thumbnailUrl = incident.getImages().get(0).getThumbnailUrl();
        }

        IncidentLocationDto locationDto = new IncidentLocationDto(
                incident.getLat(),
                incident.getLng(),
                incident.getAddressLabel(),
                incident.getArea(),
                incident.getCity(),
                incident.getGeohash()
        );

        return new IncidentListItemDto(
                incident.getId(),
                incident.getTitle(),
                incident.getCategory(),
                incident.getStatus(),
                incident.getPriority(),
                incident.getCityId(),
                thumbnailUrl,
                locationDto,
                incident.getCreatedAt().toString(),
                incident.getUpdatedAt().toString()
        );
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
