package com.example.spliteasybackend.receipts.application.internal;
import com.example.spliteasybackend.membercontributions.domain.models.aggregates.MemberContribution;
import com.example.spliteasybackend.membercontributions.infrastructure.persistance.jpa.repositories.MemberContributionRepository;
import com.example.spliteasybackend.receipts.domain.model.entities.PaymentReceipt;
import com.example.spliteasybackend.receipts.domain.model.valueobjects.PaymentReceiptStatus;
import com.example.spliteasybackend.receipts.domain.services.PaymentReceiptService;
import com.example.spliteasybackend.receipts.infrastructure.persistence.jpa.repositories.PaymentReceiptRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.time.Instant;
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
                .orElseThrow(() -> new IllegalArgumentException("MemberContribution no encontrada"));

        Long ownerId = mc.getMemberId();
        if (ownerId != null && !ownerId.equals(currentUserId)) {
            throw new SecurityException("No puedes subir boletas de otra persona");
        }

        PaymentReceipt receipt = new PaymentReceipt(mc, originalFilename, publicUrl, currentUserId);
        return receiptRepository.save(receipt);
    }

    @Override
    public List<PaymentReceipt> listByMemberContribution(Long memberContributionId,
                                                         Long currentUserId,
                                                         boolean isRepresentative) {
        MemberContribution mc = memberContributionRepository.findById(memberContributionId)
                .orElseThrow(() -> new IllegalArgumentException("MemberContribution no encontrada"));

        Long ownerId = mc.getMemberId();
        if (!isRepresentative && ownerId != null && !ownerId.equals(currentUserId)) {
            throw new SecurityException("No puedes ver boletas de otra persona");
        }
        return receiptRepository.findAllByMemberContribution_Id(memberContributionId);
    }

    @Override
    public Optional<PaymentReceipt> approve(Long receiptId, Long reviewerUserId) {
        return receiptRepository.findById(receiptId).map(r -> {
            r.setStatus(PaymentReceiptStatus.APPROVED);
            r.setReviewedByUserId(reviewerUserId);
            r.setReviewedAt(Instant.now());

            Long mcId = r.getMemberContribution().getId();
            memberContributionRepository.markPaid(mcId, "PAGADO");

            return r;
        });
    }

    @Override
    public Optional<PaymentReceipt> reject(Long receiptId, Long reviewerUserId, String notes) {
        return receiptRepository.findById(receiptId).map(r -> {
            r.setStatus(PaymentReceiptStatus.REJECTED);
            r.setReviewedByUserId(reviewerUserId);
            r.setReviewedAt(Instant.now());
            r.setNotes(notes);
            return r;
        });
    }
}
