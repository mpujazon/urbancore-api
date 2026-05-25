package com.urbancore.urbancore_api.seed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class SeedRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SeedRunner.class);

    private final SeedProperties seedProperties;
    private final ProductionDemoSeeder productionDemoSeeder;

    public SeedRunner(SeedProperties seedProperties, ProductionDemoSeeder productionDemoSeeder) {
        this.seedProperties = seedProperties;
        this.productionDemoSeeder = productionDemoSeeder;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Seed mode: {}", seedProperties.getMode());
        if (!seedProperties.isEnabled()) {
            return;
        }
        if (!seedProperties.isProductionDemoMode()) {
            log.warn("Seed enabled but mode '{}' is unsupported. Supported mode: production-demo", seedProperties.getMode());
            return;
        }
        productionDemoSeeder.seed();
    }
}
