package com.example.spliteasybackend.membercontributions.infrastructure.persistance.jpa.repositories;
import com.example.spliteasybackend.membercontributions.domain.models.aggregates.MemberContribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
public interface MemberContributionRepository extends JpaRepository<MemberContribution, Long> {
    List<MemberContribution> findByContribution_Id(Long contributionId);
    @Transactional
    void deleteByContribution_Id(Long contributionId);
}
