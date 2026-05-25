package com.urbancore.urbancore_api.seed;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "urbancore.seed")
public class SeedProperties {

    private boolean enabled = false;
    private String mode = "off";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public boolean isProductionDemoMode() {
        return "production-demo".equalsIgnoreCase(mode);
    }
}
