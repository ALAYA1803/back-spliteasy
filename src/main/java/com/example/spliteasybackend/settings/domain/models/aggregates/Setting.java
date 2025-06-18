package com.example.spliteasybackend.settings.domain.models.aggregates;

import com.example.spliteasybackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.example.spliteasybackend.settings.domain.models.commands.CreateSettingCommand;
import com.example.spliteasybackend.settings.domain.models.valueobjects.DarkMode;
import com.example.spliteasybackend.settings.domain.models.valueobjects.Language;
import com.example.spliteasybackend.settings.domain.models.valueobjects.NotificationsEnabled;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Setting extends AuditableAbstractAggregateRoot<Setting> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "language"))
    private Language language;

    @Embedded
    @AttributeOverride(name = "enabled", column = @Column(name = "dark_mode"))
    private DarkMode darkMode;

    @Embedded
    @AttributeOverride(name = "enabled", column = @Column(name = "notifications_enabled"))
    private NotificationsEnabled notificationsEnabled;

    public Setting(CreateSettingCommand command) {
        this.userId = command.userId();
        this.language = new Language(command.language());
        this.darkMode = new DarkMode(command.darkMode());
        this.notificationsEnabled = new NotificationsEnabled(command.notificationsEnabled());
    }

    public Setting() {
        // Constructor por defecto requerido por JPA
    }

    public void update(CreateSettingCommand command) {
        this.language = new Language(command.language());
        this.darkMode = new DarkMode(command.darkMode());
        this.notificationsEnabled = new NotificationsEnabled(command.notificationsEnabled());
    }
}
