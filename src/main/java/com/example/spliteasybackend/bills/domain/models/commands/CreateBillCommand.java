package com.example.spliteasybackend.bills.domain.models.commands;

import com.example.spliteasybackend.bills.domain.models.valueobjects.Money;

import java.time.LocalDate;

public record CreateBillCommand(
        Long householdId,
        String description,
        Money monto,
        Long createdBy,
        LocalDate fecha
) {
    public CreateBillCommand {
        if (householdId == null || householdId <= 0)
            throw new IllegalArgumentException("El ID del hogar debe ser un valor positivo.");

        if (createdBy == null || createdBy <= 0)
            throw new IllegalArgumentException("El ID del creador debe ser un valor positivo.");

        if (description == null || description.isBlank())
            throw new IllegalArgumentException("La descripción no puede estar vacía.");

        if (monto == null || monto.value().compareTo(Money.ZERO.value()) <= 0)
            throw new IllegalArgumentException("El monto debe ser mayor a cero.");

        if (fecha == null)
            throw new IllegalArgumentException("La fecha no puede ser nula.");
    }
}
