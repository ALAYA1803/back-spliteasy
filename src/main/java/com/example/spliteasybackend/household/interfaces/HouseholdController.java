package com.example.spliteasybackend.household.interfaces;


import com.example.spliteasybackend.household.application.dto.CreateHouseholdRequest;
import com.example.spliteasybackend.household.application.dto.HouseholdResponse;
import com.example.spliteasybackend.household.application.services.HouseholdService;
import com.example.spliteasybackend.household.domain.models.aggregates.Household;
import com.example.spliteasybackend.household.domain.models.repositories.HouseholdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/households")
@RequiredArgsConstructor
public class HouseholdController {

    private final HouseholdService householdService;

    @PostMapping
    public ResponseEntity<HouseholdResponse> create(@RequestBody CreateHouseholdRequest request) {
        HouseholdResponse household = householdService.createHousehold(request);
        return new ResponseEntity<>(household, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<HouseholdResponse>> findAll() {
        List<HouseholdResponse> households = householdService.getAllHouseholds();
        return ResponseEntity.ok(households);
    }
}
