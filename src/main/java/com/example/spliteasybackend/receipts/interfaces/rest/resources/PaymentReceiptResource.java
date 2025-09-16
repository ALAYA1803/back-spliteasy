package com.example.spliteasybackend.receipts.interfaces.rest.resources;

import com.example.spliteasybackend.receipts.domain.model.valueobjects.PaymentReceiptStatus;

import java.time.Instant;

public record PaymentReceiptResource(
        Long id,
        Long memberContributionId,
        String filename,
        String url,
        PaymentReceiptStatus status,
        Long uploaderUserId,
        Long reviewedByUserId,
        Instant uploadedAt,
        Instant reviewedAt,
        String notes
) {}
