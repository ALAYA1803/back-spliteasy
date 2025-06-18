// MemberContributionRepository.java
package com.example.spliteasybackend.membercontributions.infrastructure.persistance.jpa.repositories;

import com.example.spliteasybackend.membercontributions.domain.models.aggregates.MemberContribution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberContributionRepository extends JpaRepository<MemberContribution, Long> {
}
