package com.example.spliteasybackend.settings.interfaces.acl;

public interface SettingsContextFacade {
    Long createSetting(Long userId, String language, boolean darkMode, boolean notificationsEnabled);
    boolean existsSettingById(Long id);
}
