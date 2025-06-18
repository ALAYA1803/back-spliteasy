package com.example.spliteasybackend.settings.application.internal.commandservices;

import com.example.spliteasybackend.settings.domain.models.aggregates.Setting;
import com.example.spliteasybackend.settings.domain.models.commands.CreateSettingCommand;
import com.example.spliteasybackend.settings.domain.services.SettingCommandService;
import com.example.spliteasybackend.settings.infrastructure.persistance.jpa.repositories.SettingRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SettingCommandServiceImpl implements SettingCommandService {

    private final SettingRepository settingRepository;

    public SettingCommandServiceImpl(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    @Override
    public Optional<Setting> handle(CreateSettingCommand command) {
        var setting = new Setting(command);
        settingRepository.save(setting);
        return Optional.of(setting);
    }

    @Override
    public Optional<Setting> update(Long id, CreateSettingCommand command) {
        var optionalSetting = settingRepository.findById(id);
        if (optionalSetting.isEmpty()) return Optional.empty();

        var setting = optionalSetting.get();
        setting.update(command);

        settingRepository.save(setting);
        return Optional.of(setting);
    }

    @Override
    public boolean delete(Long id) {
        if (!settingRepository.existsById(id)) return false;
        settingRepository.deleteById(id);
        return true;
    }
}
