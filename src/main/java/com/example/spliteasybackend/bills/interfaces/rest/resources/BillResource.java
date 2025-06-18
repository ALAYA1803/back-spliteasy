package com.example.spliteasybackend.bills.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BillResource(
        Long id,
        Long householdId,
        String description,
        BigDecimal monto,
        Long createdBy,
        LocalDate fecha
) {
}
