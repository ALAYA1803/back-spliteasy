package com.example.spliteasybackend.households.interfaces.acl;

public interface HouseholdsContextFacade {
    Long createHousehold(String name, String description, String currency, Long representanteId);
    boolean existsHouseholdById(Long id);
}
