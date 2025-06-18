package com.example.spliteasybackend.membercontributions.interfaces.rest.transform;

import com.example.spliteasybackend.membercontributions.domain.models.aggregates.MemberContribution;
import com.example.spliteasybackend.membercontributions.interfaces.rest.resources.MemberContributionResource;

public class MemberContributionResourceFromEntityAssembler {

    public static MemberContributionResource toResourceFromEntity(MemberContribution entity) {
        return new MemberContributionResource(
                entity.getId(),
                entity.getContributionId(),
                entity.getMemberId(),
                entity.getMonto(),
                entity.getStatus().name(),       // Convertir enum a String
                entity.getPagadoEn()
        );
    }
}
