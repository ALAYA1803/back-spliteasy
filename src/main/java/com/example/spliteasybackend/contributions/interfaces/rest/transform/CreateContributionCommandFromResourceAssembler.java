package com.example.spliteasybackend.contributions.interfaces.rest.transform;

import com.example.spliteasybackend.contributions.domain.models.commands.CreateContributionCommand;
import com.example.spliteasybackend.contributions.domain.models.valueobjects.Strategy;
import com.example.spliteasybackend.contributions.interfaces.rest.resources.CreateContributionResource;

public class CreateContributionCommandFromResourceAssembler {

    public static CreateContributionCommand toCommandFromResource(CreateContributionResource resource) {
        return new CreateContributionCommand(
                resource.billId(),
                resource.householdId(),
                resource.description(),
                resource.fechaLimite(),
                Strategy.valueOf(resource.strategy().toUpperCase())
        );
    }
}
