package com.urbancore.urbancore_api.seed;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeedRegistryRepository extends JpaRepository<SeedRegistry, Long> {
    boolean existsBySeedKey(String seedKey);

    Optional<SeedRegistry> findBySeedKey(String seedKey);
}
