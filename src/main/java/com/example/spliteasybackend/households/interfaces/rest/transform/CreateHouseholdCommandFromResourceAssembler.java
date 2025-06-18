package com.example.spliteasybackend.households.interfaces.rest.transform;

import com.example.spliteasybackend.households.domain.models.commands.CreateHouseholdCommand;
import com.example.spliteasybackend.households.interfaces.rest.resources.CreateHouseholdResource;

public class CreateHouseholdCommandFromResourceAssembler {

    public static CreateHouseholdCommand toCommandFromResource(CreateHouseholdResource resource) {
        return new CreateHouseholdCommand(
                resource.name(),
                resource.description(),
                resource.currency(),
                resource.representanteId()
        );
    }
}
