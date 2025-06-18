package com.example.spliteasybackend.settings.domain.models.commands;

public record CreateSettingCommand(
        Long userId,
        String language,
        boolean darkMode,
        boolean notificationsEnabled
) {}
