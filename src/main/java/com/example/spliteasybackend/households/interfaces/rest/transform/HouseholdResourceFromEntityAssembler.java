package com.example.spliteasybackend.households.interfaces.rest.transform;

import com.example.spliteasybackend.households.domain.models.aggregates.Household;
import com.example.spliteasybackend.households.interfaces.rest.resources.HouseholdResource;

public class HouseholdResourceFromEntityAssembler {

    public static HouseholdResource toResourceFromEntity(Household entity) {
        return new HouseholdResource(
                entity.getId(),
                entity.getName() != null ? entity.getName().getValue() : null,
                entity.getDescription(),
                entity.getCurrency(),
                entity.getRepresentante() != null ? entity.getRepresentante().getId() : null
        );
    }
}

