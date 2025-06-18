package com.example.spliteasybackend.settings.interfaces.rest.transform;

import com.example.spliteasybackend.settings.domain.models.commands.CreateSettingCommand;
import com.example.spliteasybackend.settings.interfaces.rest.resources.CreateSettingResource;

public class CreateSettingCommandFromResourceAssembler {

    public static CreateSettingCommand toCommandFromResource(CreateSettingResource resource) {
        return new CreateSettingCommand(
                resource.userId(),
                resource.language(),
                resource.darkMode(),
                resource.notificationsEnabled()
        );
    }
}
