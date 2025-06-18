package com.example.spliteasybackend.householdmembers.interfaces.rest.resources;

public record CreateHouseholdMemberResource(
        Long userId,
        Long householdId
) {
    public CreateHouseholdMemberResource {
        if (userId == null || userId <= 0)
            throw new IllegalArgumentException("userId must be a positive number");

        if (householdId == null || householdId <= 0)
            throw new IllegalArgumentException("householdId must be a positive number");
    }
}
