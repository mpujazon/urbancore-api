package com.urbancore.urbancore_api.controllers;

import com.urbancore.urbancore_api.dtos.CreateIncidentRequest;
import com.urbancore.urbancore_api.dtos.IncidentDto;
import com.urbancore.urbancore_api.dtos.IncidentFilterDto;
import com.urbancore.urbancore_api.models.IncidentCategory;
import com.urbancore.urbancore_api.models.IncidentPriority;
import com.urbancore.urbancore_api.models.IncidentStatus;
import com.urbancore.urbancore_api.services.IncidentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IncidentDto createIncident(
            @RequestBody CreateIncidentRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return incidentService.createIncident(request, jwt);
    }

    @GetMapping
    public List<IncidentDto> getAllIncidents(
            @RequestParam(required = false) IncidentStatus status,
            @RequestParam(required = false) IncidentCategory category,
            @RequestParam(required = false) IncidentPriority priority,
            @RequestParam(required = false) String cityId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(required = false) String q
    ) {
        IncidentFilterDto filters = new IncidentFilterDto(status, category, priority, cityId, from, to, q);
        return incidentService.getAllIncidents(filters);
    }

    @GetMapping("/me")
    public List<IncidentDto> getMyIncidents(@AuthenticationPrincipal Jwt jwt) {
        return incidentService.getCurrentCitizenIncidents(jwt);
    }
}
