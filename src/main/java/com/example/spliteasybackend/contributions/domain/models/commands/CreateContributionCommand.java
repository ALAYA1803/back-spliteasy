package com.example.spliteasybackend.contributions.domain.models.commands;

import com.example.spliteasybackend.contributions.domain.models.valueobjects.Strategy;

import java.time.LocalDate;

public record CreateContributionCommand(
        Long billId,
        Long householdId,
        String description,
        LocalDate fechaLimite,
        Strategy strategy
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
    }
}
