package com.example.spliteasybackend.membercontributions.infrastructure.persistance.jpa.repositories;

import com.example.spliteasybackend.membercontributions.domain.models.aggregates.MemberContribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MemberContributionRepository extends JpaRepository<MemberContribution, Long> {

    List<MemberContribution> findByContribution_Id(Long contributionId);

    @Transactional
    void deleteByContribution_Id(Long contributionId);

    List<MemberContribution> findAllByMember_IdAndContribution_Household_Id(Long memberId, Long householdId);

    List<MemberContribution> findAllByMember_Id(Long memberId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE member_contributions " +
            "SET status = :status, pagadoEn = CURRENT_TIMESTAMP " +
            "WHERE id = :id", nativeQuery = true)
    int markPaidNative(@Param("id") Long id, @Param("status") String status);
}
