package com.example.spliteasybackend.contributions.interfaces.rest.transform;

import com.example.spliteasybackend.contributions.domain.models.aggregates.Contribution;
import com.example.spliteasybackend.contributions.interfaces.rest.resources.ContributionResource;

public class ContributionResourceFromEntityAssembler {

    public static ContributionResource toResourceFromEntity(Contribution entity) {
        return new ContributionResource(
                entity.getId(),
                entity.getBillId(),
                entity.getHouseholdId(),
                entity.getDescription(),
                entity.getStrategy().name(),       // Enum a String
                entity.getFechaLimite()
        );
    }
}
