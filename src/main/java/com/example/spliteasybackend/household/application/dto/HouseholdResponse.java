package com.example.spliteasybackend.household.application.dto;

import com.example.spliteasybackend.household.domain.models.aggregates.Household;
import lombok.Getter;

@Getter
public class HouseholdResponse {
    private Long id;
    private String name;
    private String description;
    private String currency;
    private Long representanteId;

    public HouseholdResponse(Household household) {
        this.id = household.getId();
        this.name = household.getName();
        this.description = household.getDescription();
        this.currency = household.getCurrency();
        this.representanteId = household.getRepresentanteId();
    }
}
