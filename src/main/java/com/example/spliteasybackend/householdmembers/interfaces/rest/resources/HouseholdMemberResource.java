package com.example.spliteasybackend.householdmembers.interfaces.rest.resources;

public record HouseholdMemberResource(
        Long id,
        Long userId,
        Long householdId
) {
}
