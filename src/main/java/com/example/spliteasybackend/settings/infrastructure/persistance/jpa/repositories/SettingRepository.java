package com.example.spliteasybackend.settings.infrastructure.persistance.jpa.repositories;

import com.example.spliteasybackend.settings.domain.models.aggregates.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Long> {
    Optional<Setting> findByUser_Id(Long userId);
}
