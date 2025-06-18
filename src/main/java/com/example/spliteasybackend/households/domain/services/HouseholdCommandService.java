package com.example.spliteasybackend.households.domain.services;

import com.example.spliteasybackend.households.domain.models.aggregates.Household;
import com.example.spliteasybackend.households.domain.models.commands.CreateHouseholdCommand;

import java.util.Optional;

public interface HouseholdCommandService {
    Optional<Household> handle(CreateHouseholdCommand command);
    Optional<Household> update(Long id, CreateHouseholdCommand command);
    boolean delete(Long id);
}
