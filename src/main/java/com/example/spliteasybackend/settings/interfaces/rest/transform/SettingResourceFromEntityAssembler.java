package com.example.spliteasybackend.settings.interfaces.rest.transform;

import com.example.spliteasybackend.settings.domain.models.aggregates.Setting;
import com.example.spliteasybackend.settings.interfaces.rest.resources.SettingResource;

public class SettingResourceFromEntityAssembler {

    public static SettingResource toResourceFromEntity(Setting entity) {
        return new SettingResource(
                entity.getId(),
                entity.getUserId(),
                entity.getLanguage() != null ? entity.getLanguage().code() : null,
                entity.getDarkMode() != null && entity.getDarkMode().isEnabled(),
                entity.getNotificationsEnabled() != null && entity.getNotificationsEnabled().isEnabled()
        );
    }
}
