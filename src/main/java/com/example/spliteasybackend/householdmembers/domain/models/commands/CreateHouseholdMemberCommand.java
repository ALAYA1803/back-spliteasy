package com.example.spliteasybackend.householdmembers.domain.models.commands;

public record CreateHouseholdMemberCommand(Long userId, Long householdId) {

    public CreateHouseholdMemberCommand {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser un valor positivo y no nulo.");
        }

        if (householdId == null || householdId <= 0) {
            throw new IllegalArgumentException("El ID del hogar debe ser un valor positivo y no nulo.");
        }
    }
}
