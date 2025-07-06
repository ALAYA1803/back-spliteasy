package com.example.spliteasybackend.settings.application.internal.commandservices;

import com.example.spliteasybackend.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.example.spliteasybackend.settings.domain.models.aggregates.Setting;
import com.example.spliteasybackend.settings.domain.models.commands.CreateSettingCommand;
import com.example.spliteasybackend.settings.domain.services.SettingCommandService;
import com.example.spliteasybackend.settings.infrastructure.persistance.jpa.repositories.SettingRepository;
import com.example.spliteasybackend.iam.domain.model.aggregates.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SettingCommandServiceImpl implements SettingCommandService {

    private final SettingRepository settingRepository;
    private final UserRepository userRepository;

    public SettingCommandServiceImpl(SettingRepository settingRepository, UserRepository userRepository) {
        this.settingRepository = settingRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<Setting> handle(CreateSettingCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Setting setting = new Setting(user, command);
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
