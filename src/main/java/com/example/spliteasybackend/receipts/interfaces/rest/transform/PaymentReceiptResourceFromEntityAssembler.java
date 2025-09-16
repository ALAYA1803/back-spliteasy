package com.example.spliteasybackend.receipts.interfaces.rest.transform;

import com.example.spliteasybackend.receipts.domain.model.entities.PaymentReceipt;
import com.example.spliteasybackend.receipts.interfaces.rest.resources.PaymentReceiptResource;

public class PaymentReceiptResourceFromEntityAssembler {
    public static PaymentReceiptResource toResourceFromEntity(PaymentReceipt e) {
        return new PaymentReceiptResource(
                e.getId(),
                e.getMemberContribution().getId(),
                e.getFilename(),
                e.getUrl(),
                e.getStatus(),
                e.getUploaderUserId(),
                e.getReviewedByUserId(),
                e.getUploadedAt(),
                e.getReviewedAt(),
                e.getNotes()
        );
    }
}
