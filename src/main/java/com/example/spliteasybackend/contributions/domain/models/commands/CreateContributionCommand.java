package com.example.spliteasybackend.contributions.domain.models.commands;

import java.time.LocalDate;

public record CreateContributionCommand(
        Long billId,
        Long householdId,
        String description,
        java.time.LocalDate fechaLimite,
        com.example.spliteasybackend.contributions.domain.models.valueobjects.Strategy strategy,
        java.util.List<Long> memberIds,
        String qr,
        String numero
) {
    public CreateContributionCommand {
        if (billId == null || billId <= 0)
            throw new IllegalArgumentException("El ID del bill debe ser un valor positivo.");

        if (householdId == null || householdId <= 0)
            throw new IllegalArgumentException("El ID del hogar debe ser un valor positivo.");

        if (description == null || description.isBlank())
            throw new IllegalArgumentException("La descripción no puede estar vacía.");

        if (fechaLimite == null || fechaLimite.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("La fecha límite debe ser una fecha futura.");

        if (strategy == null)
            throw new IllegalArgumentException("Debe especificarse una estrategia de contribución.");

        if (qr == null || qr.isBlank())
            throw new IllegalArgumentException("El QR no puede estar vacío.");

        if (numero == null || numero.isBlank())
            throw new IllegalArgumentException("El número no puede estar vacío.");

        // Validación para números peruanos según la regla solicitada: 9 dígitos y comienza con 9
        if (!numero.matches("^9\\d{8}$"))
            throw new IllegalArgumentException("El número debe tener 9 dígitos y comenzar con 9.");
    }
}
