package com.example.spliteasybackend.households.domain.services;

import com.example.spliteasybackend.households.domain.models.aggregates.Household;
import com.example.spliteasybackend.households.domain.models.queries.GetAllHouseholdsQuery;
import com.example.spliteasybackend.households.domain.models.queries.GetHouseholdByIdQuery;

import java.util.List;
import java.util.Optional;

public interface HouseholdQueryService {
    Optional<Household> handle(GetHouseholdByIdQuery query);
    List<Household> handle(GetAllHouseholdsQuery query);
}
