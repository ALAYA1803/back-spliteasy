package com.example.spliteasybackend.receipts.application.internal;

import com.example.spliteasybackend.membercontributions.domain.models.aggregates.MemberContribution;
import com.example.spliteasybackend.membercontributions.domain.models.valueobjects.Status;
import com.example.spliteasybackend.membercontributions.infrastructure.persistance.jpa.repositories.MemberContributionRepository;
import com.example.spliteasybackend.receipts.domain.model.entities.PaymentReceipt;
import com.example.spliteasybackend.receipts.domain.model.valueobjects.PaymentReceiptStatus;
import com.example.spliteasybackend.receipts.domain.services.PaymentReceiptService;
import com.example.spliteasybackend.receipts.infrastructure.persistence.jpa.repositories.PaymentReceiptRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PaymentReceiptServiceImpl implements PaymentReceiptService {

    private final MemberContributionRepository memberContributionRepository;
    private final PaymentReceiptRepository receiptRepository;

    public PaymentReceiptServiceImpl(MemberContributionRepository memberContributionRepository,
                                     PaymentReceiptRepository receiptRepository) {
        this.memberContributionRepository = memberContributionRepository;
        this.receiptRepository = receiptRepository;
    }

    @Override
    public PaymentReceipt uploadReceipt(Long memberContributionId, Long currentUserId,
                                        String originalFilename, String publicUrl) {
        MemberContribution mc = memberContributionRepository.findById(memberContributionId)
                .orElseThrow(() -> new EntityNotFoundException("MemberContribution " + memberContributionId + " no encontrada"));

        Long ownerId = mc.getMemberId();
        if (ownerId != null && !ownerId.equals(currentUserId)) {
            throw new AccessDeniedException("No puedes subir boletas de otra persona");
        }

        PaymentReceipt receipt = new PaymentReceipt(mc, originalFilename, publicUrl, currentUserId);
        if (receipt.getStatus() == null) receipt.setStatus(PaymentReceiptStatus.PENDING);
        return receiptRepository.save(receipt);
    }

    @Override
    public List<PaymentReceipt> listByMemberContribution(Long memberContributionId,
                                                         Long currentUserId,
                                                         boolean isRepresentative) {
        MemberContribution mc = memberContributionRepository.findById(memberContributionId)
                .orElseThrow(() -> new EntityNotFoundException("MemberContribution " + memberContributionId + " no encontrada"));

        Long ownerId = mc.getMemberId();
        if (!isRepresentative && ownerId != null && !ownerId.equals(currentUserId)) {
            throw new AccessDeniedException("No puedes ver boletas de otra persona");
        }
        return receiptRepository.findAllByMemberContribution_Id(memberContributionId);
    }

    @Override
    public Optional<PaymentReceipt> approve(Long receiptId, Long reviewerUserId) {
        return receiptRepository.findByIdWithMemberContribution(receiptId).map(receipt -> {
            if (receipt.getStatus() == PaymentReceiptStatus.REJECTED) {
                throw new IllegalStateException("La boleta ya fue rechazada, no puede aprobarse");
            }
            if (receipt.getStatus() != PaymentReceiptStatus.APPROVED) {
                receipt.setStatus(PaymentReceiptStatus.APPROVED);
                receipt.setReviewedByUserId(reviewerUserId);
                receipt.setReviewedAt(Instant.now());

                Long mcId = receipt.getMemberContribution().getId();
                memberContributionRepository.markPaid(mcId, Status.PAGADO, LocalDateTime.now());
                receiptRepository.save(receipt);
            }
            return receipt;
        });
    }

    @Override
    public Optional<PaymentReceipt> reject(Long receiptId, Long reviewerUserId, String notes) {
        return receiptRepository.findByIdWithMemberContribution(receiptId).map(receipt -> {
            if (receipt.getStatus() == PaymentReceiptStatus.APPROVED) {
                throw new IllegalStateException("La boleta ya fue aprobada, no puede rechazarse");
            }
            if (receipt.getStatus() != PaymentReceiptStatus.REJECTED) {
                receipt.setStatus(PaymentReceiptStatus.REJECTED);
                receipt.setReviewedByUserId(reviewerUserId);
                receipt.setReviewedAt(Instant.now());
                receipt.setNotes(notes);
                receiptRepository.save(receipt);
            }
            return receipt;
        });
    }
}
