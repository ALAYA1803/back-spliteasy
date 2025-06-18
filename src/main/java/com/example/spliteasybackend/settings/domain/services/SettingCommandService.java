package com.example.spliteasybackend.settings.domain.services;

import com.example.spliteasybackend.settings.domain.models.aggregates.Setting;
import com.example.spliteasybackend.settings.domain.models.commands.CreateSettingCommand;

import java.util.Optional;

public interface SettingCommandService {

    Optional<Setting> handle(CreateSettingCommand command);

    Optional<Setting> update(Long id, CreateSettingCommand command);

    boolean delete(Long id);
}
