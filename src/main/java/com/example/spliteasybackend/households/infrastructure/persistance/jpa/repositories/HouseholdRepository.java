package com.example.spliteasybackend.households.infrastructure.persistance.jpa.repositories;

import com.example.spliteasybackend.households.domain.models.aggregates.Household;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HouseholdRepository extends JpaRepository<Household, Long> {
    // No necesitas más métodos si solo consultas por ID
}
