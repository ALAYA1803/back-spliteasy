package com.example.spliteasybackend.contributions.interfaces.rest.resources;

import java.time.LocalDate;

public record CreateContributionResource(
        Long billId,
        Long householdId,
        String description,
        String strategy,
        LocalDate fechaLimite
) {
    public CreateContributionResource {
        if (billId == null || billId <= 0)
            throw new IllegalArgumentException("billId must be a positive number");

        if (householdId == null || householdId <= 0)
            throw new IllegalArgumentException("householdId must be a positive number");

        if (description == null || description.isBlank())
            throw new IllegalArgumentException("description cannot be blank");

        if (strategy == null || strategy.isBlank())
            throw new IllegalArgumentException("strategy cannot be blank");

        if (fechaLimite == null)
            throw new IllegalArgumentException("fechaLimite cannot be null");
    }
}
