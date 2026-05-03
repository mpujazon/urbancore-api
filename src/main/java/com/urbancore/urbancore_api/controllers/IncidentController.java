package com.urbancore.urbancore_api.controllers;

import com.urbancore.urbancore_api.dtos.CreateIncidentRequest;
import com.urbancore.urbancore_api.dtos.IncidentDto;
import com.urbancore.urbancore_api.services.IncidentService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/incidents"})
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

    @GetMapping("/me")
    public List<IncidentDto> getMyIncidents(@AuthenticationPrincipal Jwt jwt) {
        return incidentService.getCurrentCitizenIncidents(jwt);
    }
}
