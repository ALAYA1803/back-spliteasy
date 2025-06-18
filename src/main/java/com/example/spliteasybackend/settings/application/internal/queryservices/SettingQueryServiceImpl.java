package com.example.spliteasybackend.settings.application.internal.queryservices;

import com.example.spliteasybackend.settings.domain.models.aggregates.Setting;
import com.example.spliteasybackend.settings.domain.models.queries.GetAllSettingsQuery;
import com.example.spliteasybackend.settings.domain.models.queries.GetSettingByIdQuery;
import com.example.spliteasybackend.settings.domain.services.SettingQueryService;
import com.example.spliteasybackend.settings.infrastructure.persistance.jpa.repositories.SettingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SettingQueryServiceImpl implements SettingQueryService {

    private final SettingRepository settingRepository;

    public SettingQueryServiceImpl(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    @Override
    public Optional<Setting> handle(GetSettingByIdQuery query) {
        return settingRepository.findById(query.id());
    }

    @Override
    public List<Setting> handle(GetAllSettingsQuery query) {
        return settingRepository.findAll();
    }
}
