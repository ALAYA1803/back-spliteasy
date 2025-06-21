package com.example.spliteasybackend.settings.domain.models.aggregates;

import com.example.spliteasybackend.settings.domain.models.commands.CreateSettingCommand;
import com.example.spliteasybackend.settings.domain.models.valueobjects.DarkMode;
import com.example.spliteasybackend.settings.domain.models.valueobjects.Language;
import com.example.spliteasybackend.settings.domain.models.valueobjects.NotificationsEnabled;
import com.example.spliteasybackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.example.spliteasybackend.user.domain.models.aggregates.User;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Setting extends AuditableAbstractAggregateRoot<Setting> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Relación con User
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "language", length = 10))
    private Language language;

    @Embedded
    @AttributeOverride(name = "enabled", column = @Column(name = "dark_mode"))
    private DarkMode darkMode;

    @Embedded
    @AttributeOverride(name = "enabled", column = @Column(name = "notifications_enabled"))
    private NotificationsEnabled notificationsEnabled;

    protected Setting() {
        // Constructor por defecto requerido por JPA
    }

    public Setting(User user, CreateSettingCommand command) {
        if (user == null) throw new IllegalArgumentException("El usuario no puede ser nulo");

        this.user = user;
        this.language = new Language(command.language());
        this.darkMode = new DarkMode(command.darkMode());
        this.notificationsEnabled = new NotificationsEnabled(command.notificationsEnabled());
    }

    public void update(CreateSettingCommand command) {
        this.language = new Language(command.language());
        this.darkMode = new DarkMode(command.darkMode());
        this.notificationsEnabled = new NotificationsEnabled(command.notificationsEnabled());
    }

    public Long getUserId() {
        return user != null ? user.getId() : null;
    }
}
