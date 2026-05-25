package com.urbancore.urbancore_api.city.service;

import com.urbancore.urbancore_api.city.dto.CityResponse;
import com.urbancore.urbancore_api.city.entity.City;
import com.urbancore.urbancore_api.city.repository.CityRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CityService {

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public List<CityResponse> findAll() {
        return cityRepository.findAll(Sort.by("name").ascending())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CityResponse findBySlug(String slug) {
        return cityRepository.findBySlug(slug)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "City not found"));
    }

    private CityResponse toResponse(City city) {
        return new CityResponse(city.getId(), city.getName(), city.getSlug());
    }
}
