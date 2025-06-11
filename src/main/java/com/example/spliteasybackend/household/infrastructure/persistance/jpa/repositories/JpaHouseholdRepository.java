package com.example.spliteasybackend.household.infrastructure.persistance.jpa.repositories;

import com.example.spliteasybackend.household.domain.models.aggregates.Household;
import com.example.spliteasybackend.household.domain.models.repositories.HouseholdRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaHouseholdRepository extends JpaRepository<Household, Long>, HouseholdRepository {
}
