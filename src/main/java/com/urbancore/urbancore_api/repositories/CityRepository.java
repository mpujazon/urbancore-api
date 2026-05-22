package com.urbancore.urbancore_api.repositories;

import com.urbancore.urbancore_api.models.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CityRepository extends JpaRepository<City, UUID> {
    boolean existsBySlug(String slug);

    Optional<City> findBySlug(String slug);
}
