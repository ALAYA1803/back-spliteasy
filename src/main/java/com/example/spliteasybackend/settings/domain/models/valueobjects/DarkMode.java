package com.example.spliteasybackend.settings.domain.models.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public class DarkMode {

    private boolean enabled;

    protected DarkMode() {
    }

    public DarkMode(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
