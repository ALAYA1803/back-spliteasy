package com.example.spliteasybackend.householdmembers.interfaces.rest.transform;

import com.example.spliteasybackend.householdmembers.domain.models.commands.CreateHouseholdMemberCommand;
import com.example.spliteasybackend.householdmembers.interfaces.rest.resources.CreateHouseholdMemberResource;

public class CreateHouseholdMemberCommandFromResourceAssembler {

    public static CreateHouseholdMemberCommand toCommandFromResource(CreateHouseholdMemberResource resource) {
        return new CreateHouseholdMemberCommand(
                resource.userId(),
                resource.householdId()
        );
    }
}
