package com.example.spliteasybackend.contributions.domain.models.commands;

import com.example.spliteasybackend.contributions.domain.models.valueobjects.Strategy;

import java.time.LocalDate;
import java.util.List;

public record CreateContributionCommand(
        Long billId,
        Long householdId,
        String description,
        java.time.LocalDate fechaLimite,
        com.example.spliteasybackend.contributions.domain.models.valueobjects.Strategy strategy,
        java.util.List<Long> memberIds
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
