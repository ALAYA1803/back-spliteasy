package com.example.spliteasybackend.householdmembers.domain.services;

import com.example.spliteasybackend.householdmembers.domain.models.aggregates.HouseholdMember;
import com.example.spliteasybackend.householdmembers.domain.models.commands.CreateHouseholdMemberCommand;

import java.util.Optional;

public interface HouseholdMemberCommandService {
    Optional<HouseholdMember> handle(CreateHouseholdMemberCommand command);
    Optional<HouseholdMember> update(Long id, CreateHouseholdMemberCommand command);
    boolean delete(Long id);
}
