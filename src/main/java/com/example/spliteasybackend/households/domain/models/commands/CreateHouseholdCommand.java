package com.example.spliteasybackend.households.domain.models.commands;

public record CreateHouseholdCommand(
        String name,
        String description,
        String currency,
        Long representanteId
) {}
