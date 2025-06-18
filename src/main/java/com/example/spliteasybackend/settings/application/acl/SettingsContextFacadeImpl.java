package com.example.spliteasybackend.settings.application.acl;

import com.example.spliteasybackend.settings.domain.models.commands.CreateSettingCommand;
import com.example.spliteasybackend.settings.domain.models.queries.GetSettingByIdQuery;
import com.example.spliteasybackend.settings.domain.services.SettingCommandService;
import com.example.spliteasybackend.settings.domain.services.SettingQueryService;
import com.example.spliteasybackend.settings.interfaces.acl.SettingsContextFacade;
import org.springframework.stereotype.Service;

@Service
public class SettingsContextFacadeImpl implements SettingsContextFacade {

    private final SettingCommandService settingCommandService;
    private final SettingQueryService settingQueryService;

    public SettingsContextFacadeImpl(SettingCommandService settingCommandService,
                                     SettingQueryService settingQueryService) {
        this.settingCommandService = settingCommandService;
        this.settingQueryService = settingQueryService;
    }

    @Override
    public Long createSetting(Long userId, String language, boolean darkMode, boolean notificationsEnabled) {
        var command = new CreateSettingCommand(userId, language, darkMode, notificationsEnabled);
        var setting = settingCommandService.handle(command);
        return setting.map(s -> s.getId()).orElse(0L);
    }

    @Override
    public boolean existsSettingById(Long id) {
        var query = new GetSettingByIdQuery(id);
        return settingQueryService.handle(query).isPresent();
    }
}
