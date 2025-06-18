package com.example.spliteasybackend.households.interfaces.rest.resources;

public record CreateHouseholdResource(
        String name,
        String description,
        String currency,
        Long representanteId
) {
    public CreateHouseholdResource {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("name cannot be blank");

        if (description == null || description.isBlank())
            throw new IllegalArgumentException("description cannot be blank");

        if (currency == null || currency.isBlank())
            throw new IllegalArgumentException("currency cannot be blank");

        if (representanteId == null || representanteId <= 0)
            throw new IllegalArgumentException("representanteId must be a positive number");
    }
}
