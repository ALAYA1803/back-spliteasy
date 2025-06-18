package com.example.spliteasybackend.membercontributions.interfaces.rest.transform;

import com.example.spliteasybackend.membercontributions.domain.models.commands.CreateMemberContributionCommand;
import com.example.spliteasybackend.membercontributions.domain.models.valueobjects.Status;
import com.example.spliteasybackend.membercontributions.interfaces.rest.resources.CreateMemberContributionResource;

public class CreateMemberContributionCommandFromResourceAssembler {

    public static CreateMemberContributionCommand toCommandFromResource(CreateMemberContributionResource resource) {
        return new CreateMemberContributionCommand(
                resource.contributionId(),
                resource.memberId(),
                resource.monto(),
                Status.valueOf(resource.status().toUpperCase()),
                resource.pagadoEn()
        );
    }
}
