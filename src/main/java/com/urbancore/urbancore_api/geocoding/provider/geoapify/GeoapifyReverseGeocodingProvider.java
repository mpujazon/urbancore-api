package com.urbancore.urbancore_api.geocoding.provider.geoapify;

import com.urbancore.urbancore_api.geocoding.dto.ReverseGeocodingResponse;
import com.urbancore.urbancore_api.geocoding.provider.ReverseGeocodingProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class GeoapifyReverseGeocodingProvider implements ReverseGeocodingProvider {

    private final RestClient restClient;
    private final GeoapifyReverseGeocodingMapper mapper;
    private final String apiKey;

    public GeoapifyReverseGeocodingProvider(RestClient.Builder restClientBuilder,
                                            GeoapifyReverseGeocodingMapper mapper,
                                            @Value("${geoapify.api-key}") String apiKey) {
        this.restClient = restClientBuilder
                .baseUrl("https://api.geoapify.com/v1/geocode/reverse")
                .build();
        this.mapper = mapper;
        this.apiKey = apiKey;
    }

    @Override
    public ReverseGeocodingResponse reverse(double lat, double lng) {
        try {
            GeoapifyReverseGeocodingResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("lat", lat)
                            .queryParam("lon", lng)
                            .queryParam("format", "json")
                            .queryParam("apiKey", apiKey)
                            .build())
                    .retrieve()
                    .body(GeoapifyReverseGeocodingResponse.class);

            if (response == null || response.results() == null || response.results().isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "ADDRESS_NOT_FOUND: No address found for the provided coordinates"
                );
            }

            GeoapifyReverseGeocodingResult firstResult = response.results().getFirst();
            return mapper.toNormalized(lat, lng, firstResult, response.citySlug());
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (RestClientException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "EXTERNAL_SERVICE_ERROR: Reverse geocoding provider is unavailable"
            );
        }
    }
}
