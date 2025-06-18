package com.example.spliteasybackend.contributions.domain.models.commands;

import com.example.spliteasybackend.contributions.domain.models.valueobjects.Strategy;

import java.time.LocalDate;

public record CreateContributionCommand(
        Long billId,
        Long householdId,
        String description,
        LocalDate fechaLimite,
        Strategy strategy
) {}
