package com.urbancore.urbancore_api.services;

import com.urbancore.urbancore_api.dtos.*;
import com.urbancore.urbancore_api.mappers.PublicIncidentMapper;
import com.urbancore.urbancore_api.mappers.PlannedActionMapper;
import com.urbancore.urbancore_api.models.*;
import com.urbancore.urbancore_api.repositories.CityRepository;
import com.urbancore.urbancore_api.repositories.IncidentRepository;
import com.urbancore.urbancore_api.repositories.IncidentSpecification;
import com.urbancore.urbancore_api.repositories.projections.IncidentPlannedActionsCountProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final CityRepository cityRepository;
    private final CurrentUserService currentUserService;
    private final PublicIncidentMapper publicIncidentMapper;
    private final PlannedActionMapper plannedActionMapper;

    public IncidentService(
            IncidentRepository incidentRepository,
            CityRepository cityRepository,
            CurrentUserService currentUserService,
            PublicIncidentMapper publicIncidentMapper,
            PlannedActionMapper plannedActionMapper
    ) {
        this.incidentRepository = incidentRepository;
        this.cityRepository = cityRepository;
        this.currentUserService = currentUserService;
        this.publicIncidentMapper = publicIncidentMapper;
        this.plannedActionMapper = plannedActionMapper;
    }

    @Transactional
    public IncidentDto createIncident(CreateIncidentRequest request, Jwt jwt) {
        validateRequest(request);

        User reporter = currentUserService.getCurrentUser(jwt);

        Incident incident = new Incident();
        incident.setTitle(request.title().trim());
        incident.setDescription(request.description().trim());
        incident.setCategory(request.category());
        incident.setStatus(IncidentStatus.NEW);
        incident.setPriority(request.priority() != null ? request.priority() : IncidentPriority.UNDEFINED);
        incident.setCityId(resolveCityId(request));
        incident.setReporter(reporter);

        incident.setLat(request.location().lat());
        incident.setLng(request.location().lng());
        incident.setAddressLabel(request.location().addressLabel());
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
        initialHistory.setFromStatus(null);
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

    public PublicIncidentDetailResponse getPublicIncidentDetailById(String id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Incident not found"));

        return publicIncidentMapper.toDetailResponse(incident);
    }

    public void deleteIncident(String id, Jwt jwt) {
        User currentUser = currentUserService.getCurrentUser(jwt);

        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Incident not found"));

        if (incident.getStatus() != IncidentStatus.NEW) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only incidents in NEW status can be deleted"
            );
        }

        boolean isAdmin = currentUser.getRole() == UserRole.ROLE_ADMIN;
        if (isAdmin) {
            incidentRepository.delete(incident);
            return;
        }

        boolean isOwner = incident.getReporter() != null
                && incident.getReporter().getId() != null
                && incident.getReporter().getId().equals(currentUser.getId());

        if (!isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this incident");
        }

        incidentRepository.delete(incident);
    }

    public IncidentDto updateIncidentStatus(String id, UpdateIncidentStatusRequest request, Jwt jwt) {
        if (request == null || request.status() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status is required");
        }
        if (request.status() == IncidentStatus.NULL) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status must be a valid incident status");
        }

        User currentUser = currentUserService.getCurrentUser(jwt);

        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Incident not found"));

        IncidentStatus currentStatus = incident.getStatus();
        IncidentStatus nextStatus = request.status();

        if (currentStatus != nextStatus) {
            incident.setStatus(nextStatus);

            List<IncidentStatusHistory> history = incident.getStatusHistory();
            if (history == null) {
                history = new ArrayList<>();
                incident.setStatusHistory(history);
            }

            String reason = request.reason();
            if (reason != null) {
                reason = reason.trim();
                if (reason.isBlank()) {
                    reason = null;
                }
            }

            IncidentStatusHistory historyEntry = new IncidentStatusHistory();
            historyEntry.setId(UUID.randomUUID().toString());
            historyEntry.setFromStatus(currentStatus);
            historyEntry.setToStatus(nextStatus);
            historyEntry.setChangedBy(String.valueOf(currentUser.getId()));
            historyEntry.setReason(reason);
            historyEntry.setChangedAt(Instant.now());
            historyEntry.setIncident(incident);
            history.add(historyEntry);
        }

        Incident updated = incidentRepository.save(incident);
        return toDto(updated);
    }

    public IncidentDto updateIncidentPriority(String id, UpdateIncidentPriorityRequest request) {
        if (request == null || request.priority() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "priority is required");
        }

        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Incident not found"));

        incident.setPriority(request.priority());
        Incident updated = incidentRepository.save(incident);
        return toDto(updated);
    }

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt", "updatedAt", "status", "priority", "category", "title"
    );
    private static final Set<String> ALLOWED_ADMIN_SORT_FIELDS = Set.of(
            "createdAt", "title", "category", "priority", "status"
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

    public PagedResponseDto<AdminIncidentListItemDto> getAdminIncidents(
            int page,
            int size,
            String sortParam,
            String search,
            IncidentStatus status,
            IncidentCategory category,
            IncidentPriority priority,
            String dateFrom,
            String dateTo
    ) {
        validateAdminPagination(page, size);
        Sort sort = parseAdminSort(sortParam);

        Instant from = parseDateFrom(dateFrom);
        Instant to = parseDateTo(dateTo);
        if (from != null && to != null && from.isAfter(to)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dateFrom must be before or equal to dateTo");
        }

        IncidentFilterDto filters = new IncidentFilterDto(status, category, priority, null, from, to, search);
        Specification<Incident> spec = IncidentSpecification.withAdminFilters(filters);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Incident> resultPage = incidentRepository.findAll(spec, pageable);
        Map<String, Long> plannedActionsByIncidentId = loadPlannedActionsCount(resultPage.getContent());

        List<AdminIncidentListItemDto> content = resultPage.getContent().stream()
                .map(incident -> toAdminListItemDto(incident, plannedActionsByIncidentId.getOrDefault(incident.getId(), 0L)))
                .toList();

        List<SortDto> sortDtos = sort.stream()
                .map(o -> new SortDto(o.getProperty(), o.getDirection().name().toLowerCase()))
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

    public AdminIncidentDetailResponse getAdminIncidentDetailById(String id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Incident not found"));

        IncidentReporterDto reporterDto = null;
        if (incident.getReporter() != null) {
            reporterDto = new IncidentReporterDto(
                    String.valueOf(incident.getReporter().getId()),
                    incident.getReporter().getEmail(),
                    incident.getReporter().getRole()
            );
        }

        IncidentLocationDto locationDto = new IncidentLocationDto(
                incident.getLat(),
                incident.getLng(),
                incident.getAddressLabel(),
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

        List<PlannedActionResponse> plannedActionResponses = incident.getPlannedActions().stream()
                .map(plannedActionMapper::toResponse)
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

        return new AdminIncidentDetailResponse(
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
                plannedActionResponses,
                statusHistoryDtos,
                incident.getCreatedAt().toString(),
                incident.getUpdatedAt().toString()
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

    private void validateAdminPagination(int page, int size) {
        if (page < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "page must not be negative");
        }
        if (!Set.of(10, 25, 50).contains(size)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "size must be one of: 10, 25, 50");
        }
    }

    private Sort parseAdminSort(String sortParam) {
        if (sortParam == null || sortParam.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }

        String[] parts = sortParam.split(",");
        String field = parts[0].trim();
        String rawDirection = parts.length > 1 ? parts[1].trim().toLowerCase() : "desc";

        if (!ALLOWED_ADMIN_SORT_FIELDS.contains(field)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sort field must be one of: createdAt, title, category, priority, status");
        }
        if (!"asc".equals(rawDirection) && !"desc".equals(rawDirection)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sort direction must be 'asc' or 'desc'");
        }

        Sort.Direction direction = "asc".equals(rawDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, field);
    }

    private Instant parseDateFrom(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Instant.parse(value);
        } catch (Exception ignored) {
        }

        try {
            return LocalDate.parse(value).atStartOfDay().toInstant(ZoneOffset.UTC);
        } catch (Exception ignored) {
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dateFrom must be ISO-8601 date-time or yyyy-MM-dd");
    }

    private Instant parseDateTo(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Instant.parse(value);
        } catch (Exception ignored) {
        }

        try {
            return LocalDate.parse(value).plusDays(1).atStartOfDay().minusNanos(1).toInstant(ZoneOffset.UTC);
        } catch (Exception ignored) {
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dateTo must be ISO-8601 date-time or yyyy-MM-dd");
    }

    private Map<String, Long> loadPlannedActionsCount(List<Incident> incidents) {
        if (incidents.isEmpty()) {
            return Map.of();
        }

        List<String> incidentIds = incidents.stream().map(Incident::getId).toList();
        List<IncidentPlannedActionsCountProjection> rows = incidentRepository.countPlannedActionsByIncidentIds(incidentIds);
        Map<String, Long> counts = new HashMap<>();
        for (IncidentPlannedActionsCountProjection row : rows) {
            counts.put(row.getIncidentId(), row.getPlannedActionsCount());
        }
        return counts;
    }

    private AdminIncidentListItemDto toAdminListItemDto(Incident incident, long linkedPlannedActionsCount) {
        String thumbnailUrl = null;
        if (incident.getImages() != null && !incident.getImages().isEmpty()) {
            thumbnailUrl = incident.getImages().get(0).getThumbnailUrl();
        }

        String reporterId = null;
        String reporterDisplayName = null;
        if (incident.getReporter() != null && incident.getReporter().getId() != null) {
            reporterId = String.valueOf(incident.getReporter().getId());
            reporterDisplayName = incident.getReporter().getEmail();
        }

        return new AdminIncidentListItemDto(
                incident.getId(),
                incident.getTitle(),
                incident.getCategory(),
                incident.getStatus(),
                incident.getPriority(),
                incident.getCityId(),
                reporterId,
                reporterDisplayName,
                thumbnailUrl,
                incident.getCreatedAt().toString(),
                incident.getUpdatedAt().toString(),
                linkedPlannedActionsCount
        );
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

        if ((request.cityId() == null || request.cityId().isBlank())
                && (request.citySlug() == null || request.citySlug().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cityId or citySlug is required");
        }

        boolean hasInvalidImage = request.images().stream().anyMatch(i ->
                i == null || i.url() == null || i.url().isBlank() || i.publicId() == null || i.publicId().isBlank());
        if (hasInvalidImage) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "images[].url and images[].publicId are required");
        }
    }

    private String resolveCityId(CreateIncidentRequest request) {
        if (request.cityId() != null && !request.cityId().isBlank()) {
            return request.cityId().trim();
        }

        String citySlug = request.citySlug().trim();
        return cityRepository.findBySlug(citySlug)
                .orElseGet(() -> createCity(request, citySlug))
                .getId()
                .toString();
    }

    private City createCity(CreateIncidentRequest request, String citySlug) {
        if (request.location().city() == null || request.location().city().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "location.city is required when creating a city from citySlug");
        }

        City city = new City();
        city.setName(request.location().city().trim());
        city.setSlug(citySlug);

        return cityRepository.save(city);
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
