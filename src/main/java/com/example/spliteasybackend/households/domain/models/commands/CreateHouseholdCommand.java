package com.example.spliteasybackend.households.domain.models.commands;

import java.util.Set;

public record CreateHouseholdCommand(
        String name,
        String description,
        String currency,
        Long representanteId
) {
    private static final Set<String> SUPPORTED_CURRENCIES = Set.of("USD", "PEN", "EUR", "MXN");

    public CreateHouseholdCommand {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del hogar no puede estar vacío");
        }

        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción del hogar no puede estar vacía");
        }

        if (currency == null || !SUPPORTED_CURRENCIES.contains(currency)) {
            throw new IllegalArgumentException("Moneda no soportada: " + currency);
        }

        if (representanteId == null || representanteId <= 0) {
            throw new IllegalArgumentException("El ID del representante es obligatorio y debe ser positivo");
        }
    }
}
