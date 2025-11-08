package com.example.spliteasybackend.contributions.interfaces.rest.resources;

import java.time.LocalDate;

public record CreateContributionResource(
        Long billId,
        Long householdId,
        String description,
        String strategy,
        LocalDate fechaLimite,
        java.util.List<Long> memberIds,
        String qr,
        String numero
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

        // qr can be optional (depends on requirements). If mandatory uncomment the following:
        // if (qr == null || qr.isBlank())
        //     throw new IllegalArgumentException("qr cannot be blank");

        if (numero == null || numero.isBlank())
            throw new IllegalArgumentException("numero cannot be blank");

        // Validate Peru phone number: must be 9 digits and start with 9
        if (!numero.matches("^9\\d{8}$"))
            throw new IllegalArgumentException("numero must have 9 digits and start with 9");
    }
}
