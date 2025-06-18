package com.example.spliteasybackend.bills.domain.models.commands;

import com.example.spliteasybackend.bills.domain.models.valueobjects.Money;

import java.time.LocalDate;

public record CreateBillCommand(
        Long householdId,
        String description,
        Money monto,
        Long createdBy,
        LocalDate fecha
) {}
