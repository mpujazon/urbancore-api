package com.urbancore.urbancore_api.seed;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeedRegistryService {

    private final SeedRegistryRepository seedRegistryRepository;

    public SeedRegistryService(SeedRegistryRepository seedRegistryRepository) {
        this.seedRegistryRepository = seedRegistryRepository;
    }

    @Transactional(readOnly = true)
    public boolean isSeeded(String seedKey) {
        return seedRegistryRepository.existsBySeedKey(seedKey);
    }

    @Transactional
    public void register(String seedKey, String entityType, String entityId) {
        if (seedRegistryRepository.existsBySeedKey(seedKey)) {
            return;
        }
        SeedRegistry registry = new SeedRegistry();
        registry.setSeedKey(seedKey);
        registry.setEntityType(entityType);
        registry.setEntityId(entityId);
        seedRegistryRepository.save(registry);
    }
}
