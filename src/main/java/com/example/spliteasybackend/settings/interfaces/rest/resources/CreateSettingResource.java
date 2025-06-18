package com.example.spliteasybackend.settings.interfaces.rest.resources;

public record CreateSettingResource(
        Long userId,
        String language,
        Boolean darkMode,
        Boolean notificationsEnabled
) {
    public CreateSettingResource {
        if (userId == null || userId <= 0)
            throw new IllegalArgumentException("userId must be a positive number");
        if (language == null || language.isBlank())
            throw new IllegalArgumentException("language cannot be blank");
        if (darkMode == null)
            throw new IllegalArgumentException("darkMode must not be null");
        if (notificationsEnabled == null)
            throw new IllegalArgumentException("notificationsEnabled must not be null");
    }
}
