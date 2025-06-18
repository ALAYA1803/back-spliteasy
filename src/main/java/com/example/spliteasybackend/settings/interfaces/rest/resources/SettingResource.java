package com.example.spliteasybackend.settings.interfaces.rest.resources;

public record SettingResource(
        Long id,
        Long userId,
        String language,
        Boolean darkMode,
        Boolean notificationsEnabled
) {
}
