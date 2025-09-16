package com.example.spliteasybackend.receipts.domain.services;

import com.example.spliteasybackend.receipts.domain.model.entities.PaymentReceipt;

import java.util.List;
import java.util.Optional;

public interface PaymentReceiptService {
    PaymentReceipt uploadReceipt(Long memberContributionId, Long currentUserId, String originalFilename, String publicUrl);
    List<PaymentReceipt> listByMemberContribution(Long memberContributionId, Long currentUserId, boolean isRepresentative);
    Optional<PaymentReceipt> approve(Long receiptId, Long reviewerUserId);
    Optional<PaymentReceipt> reject(Long receiptId, Long reviewerUserId, String notes);
}
