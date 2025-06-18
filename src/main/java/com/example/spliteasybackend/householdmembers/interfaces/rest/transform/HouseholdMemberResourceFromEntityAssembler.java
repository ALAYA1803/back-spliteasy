package com.example.spliteasybackend.householdmembers.interfaces.rest.transform;

import com.example.spliteasybackend.householdmembers.domain.models.aggregates.HouseholdMember;
import com.example.spliteasybackend.householdmembers.interfaces.rest.resources.HouseholdMemberResource;

public class HouseholdMemberResourceFromEntityAssembler {

    public static HouseholdMemberResource toResourceFromEntity(HouseholdMember entity) {
        return new HouseholdMemberResource(
                entity.getId(),
                entity.getUserId(),
                entity.getHouseholdId()
        );
    }
}
