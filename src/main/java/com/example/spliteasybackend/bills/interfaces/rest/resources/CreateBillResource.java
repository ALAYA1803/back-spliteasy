package com.example.spliteasybackend.bills.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateBillResource(
        Long householdId,
        String description,
        BigDecimal monto,
        Long createdBy,
        LocalDate fecha
) {
    public CreateBillResource {
        if (householdId == null || householdId <= 0)
            throw new IllegalArgumentException("householdId must be a positive number");

        if (description == null || description.isBlank())
            throw new IllegalArgumentException("description cannot be blank");

        if (monto == null || monto.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("monto must be zero or positive");

        if (createdBy == null || createdBy <= 0)
            throw new IllegalArgumentException("createdBy must be a positive number");

        if (fecha == null)
            throw new IllegalArgumentException("fecha must not be null");
    }
}
