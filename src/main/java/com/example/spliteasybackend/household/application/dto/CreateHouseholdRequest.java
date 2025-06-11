package com.example.spliteasybackend.household.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateHouseholdRequest {
    private Long id;
    private String name;
    private String description;
    private String currency;
    private Long representanteId;
}
