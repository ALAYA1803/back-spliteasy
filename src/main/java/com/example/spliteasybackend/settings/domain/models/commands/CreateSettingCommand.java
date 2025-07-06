package com.example.spliteasybackend.settings.domain.models.commands;

public record CreateSettingCommand(
        Long userId,
        String language,
        boolean darkMode,
        boolean notificationsEnabled
) {
    public CreateSettingCommand {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("El ID del usuario es obligatorio y debe ser positivo");
        }

        if (language == null || language.trim().isEmpty()) {
            throw new IllegalArgumentException("El lenguaje es obligatorio");
        }

        // Si quisieras restringir a ciertos idiomas:
        var supportedLanguages = java.util.List.of("es", "en", "fr");
        if (!supportedLanguages.contains(language)) {
            throw new IllegalArgumentException("Idioma no soportado: " + language);
        }
    }
}
