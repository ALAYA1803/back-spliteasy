package com.example.spliteasybackend.receipts.infrastructure.persistence.jpa.repositories;

import com.example.spliteasybackend.receipts.domain.model.entities.PaymentReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentReceiptRepository extends JpaRepository<PaymentReceipt, Long> {

    List<PaymentReceipt> findAllByMemberContribution_Id(Long memberContributionId);

    @Query("select r from PaymentReceipt r join fetch r.memberContribution mc where r.id = :id")
    Optional<PaymentReceipt> findByIdWithMemberContribution(@Param("id") Long id);
}
