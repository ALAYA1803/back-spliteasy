package com.example.spliteasybackend.households.interfaces.rest.resources;

public record HouseholdResource(
        Long id,
        String name,
        String description,
        String currency,
        Long representanteId
) {
}
