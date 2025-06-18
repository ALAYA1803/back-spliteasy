package com.example.spliteasybackend.householdmembers.interfaces.acl;

public interface HouseholdMembersContextFacade {
    Long createHouseholdMember(Long userId, Long householdId);
    boolean existsHouseholdMemberById(Long id);
}
