package com.example.spliteasybackend.household.application.services;


import com.example.spliteasybackend.household.application.dto.CreateHouseholdRequest;
import com.example.spliteasybackend.household.application.dto.HouseholdResponse;
import com.example.spliteasybackend.household.domain.models.aggregates.Household;
import com.example.spliteasybackend.household.domain.models.repositories.HouseholdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HouseholdService {

    private final HouseholdRepository householdRepository;

    public HouseholdResponse createHousehold(CreateHouseholdRequest request) {
        Household household = new Household(
                request.getName(),
                request.getDescription(),
                request.getCurrency(),
                request.getRepresentanteId()
        );
        Household saved = householdRepository.save(household);
        return new HouseholdResponse(saved);
    }

    public List<HouseholdResponse> getAllHouseholds() {
        return householdRepository.findAll()
                .stream()
                .map(HouseholdResponse::new)
                .collect(Collectors.toList());
    }
}
