package com.example.spliteasybackend.householdmembers.infrastructure.persistance.jpa.repositories;

import com.example.spliteasybackend.householdmembers.domain.models.aggregates.HouseholdMember;
import com.example.spliteasybackend.households.domain.models.aggregates.Household;
import com.example.spliteasybackend.iam.domain.model.aggregates.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HouseholdMemberRepository extends JpaRepository<HouseholdMember, Long> {

    boolean existsByHouseholdAndUser(Household household, User user);

    // 🔍 Este es el que te faltaba para ContributionCommandServiceImpl
    List<HouseholdMember> findAllByHousehold_Id(Long householdId);
}
