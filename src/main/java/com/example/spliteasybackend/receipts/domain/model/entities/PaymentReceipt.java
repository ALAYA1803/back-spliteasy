package com.example.spliteasybackend.receipts.domain.model.entities;

import com.example.spliteasybackend.membercontributions.domain.models.aggregates.MemberContribution;
import com.example.spliteasybackend.receipts.domain.model.valueobjects.PaymentReceiptStatus;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "payment_receipts")
public class PaymentReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_contribution_id")
    private MemberContribution memberContribution;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false, length = 512)
    private String url;

    @Column(nullable = false)
    private Long uploaderUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private PaymentReceiptStatus status = PaymentReceiptStatus.PENDING;

    private Long reviewedByUserId;
    @Column(nullable = false)
    private Instant uploadedAt = Instant.now();
    private Instant reviewedAt;

    @Lob
    private String notes;

    public PaymentReceipt() {}

    public PaymentReceipt(MemberContribution mc, String filename, String url, Long uploaderUserId) {
        this.memberContribution = mc;
        this.filename = filename;
        this.url = url;
        this.uploaderUserId = uploaderUserId;
        this.status = PaymentReceiptStatus.PENDING;
        this.uploadedAt = Instant.now();
    }

    public Long getId() { return id; }
    public MemberContribution getMemberContribution() { return memberContribution; }
    public void setMemberContribution(MemberContribution memberContribution) { this.memberContribution = memberContribution; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public Long getUploaderUserId() { return uploaderUserId; }
    public void setUploaderUserId(Long uploaderUserId) { this.uploaderUserId = uploaderUserId; }
    public PaymentReceiptStatus getStatus() { return status; }
    public void setStatus(PaymentReceiptStatus status) { this.status = status; }
    public Long getReviewedByUserId() { return reviewedByUserId; }
    public void setReviewedByUserId(Long reviewedByUserId) { this.reviewedByUserId = reviewedByUserId; }
    public Instant getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(Instant uploadedAt) { this.uploadedAt = uploadedAt; }
    public Instant getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(Instant reviewedAt) { this.reviewedAt = reviewedAt; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
