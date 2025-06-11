package com.example.spliteasybackend.household.domain.models.repositories;

import com.example.spliteasybackend.household.domain.models.aggregates.Household;

import java.util.List;

public interface HouseholdRepository {
    Household save(Household household);
    List<Household> findAll();
}
