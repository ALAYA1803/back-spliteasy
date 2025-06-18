package com.example.spliteasybackend.contributions.infrastructure.persistance.jpa.repositories;

import com.example.spliteasybackend.contributions.domain.models.aggregates.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContributionRepository extends JpaRepository<Contribution, Long> {
    // No necesitas más métodos si solo trabajas por ID
}
