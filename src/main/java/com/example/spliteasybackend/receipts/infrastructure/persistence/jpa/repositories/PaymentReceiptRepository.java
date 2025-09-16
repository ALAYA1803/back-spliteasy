package com.example.spliteasybackend.receipts.infrastructure.persistence.jpa.repositories;

import com.example.spliteasybackend.receipts.domain.model.entities.PaymentReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentReceiptRepository extends JpaRepository<PaymentReceipt, Long> {
    List<PaymentReceipt> findAllByMemberContribution_Id(Long memberContributionId);
}
