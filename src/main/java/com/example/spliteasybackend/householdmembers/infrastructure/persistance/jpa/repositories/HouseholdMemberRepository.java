package com.example.spliteasybackend.householdmembers.infrastructure.persistance.jpa.repositories;

import com.example.spliteasybackend.householdmembers.domain.models.aggregates.HouseholdMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HouseholdMemberRepository extends JpaRepository<HouseholdMember, Long> {
    // No necesitas más métodos si solo consultas por ID
}
