package com.example.spliteasybackend.householdmembers.domain.services;

import com.example.spliteasybackend.householdmembers.domain.models.aggregates.HouseholdMember;
import com.example.spliteasybackend.householdmembers.domain.models.queries.GetAllHouseholdMembersQuery;
import com.example.spliteasybackend.householdmembers.domain.models.queries.GetHouseholdMemberByIdQuery;

import java.util.List;
import java.util.Optional;

public interface HouseholdMemberQueryService {
    Optional<HouseholdMember> handle(GetHouseholdMemberByIdQuery query);
    List<HouseholdMember> handle(GetAllHouseholdMembersQuery query);
}
