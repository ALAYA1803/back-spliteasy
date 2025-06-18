package com.example.spliteasybackend.settings.domain.models.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public class NotificationsEnabled {

    private boolean enabled;

    protected NotificationsEnabled() {
    }

    public NotificationsEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
