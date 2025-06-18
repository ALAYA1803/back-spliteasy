package com.example.spliteasybackend.contributions.interfaces.rest.resources;

import java.time.LocalDate;

public record ContributionResource(
        Long id,
        Long billId,
        Long householdId,
        String description,
        String strategy,
        LocalDate fechaLimite
) {
}
