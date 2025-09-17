package com.example.spliteasybackend.receipts.interfaces.rest;

import com.example.spliteasybackend.receipts.domain.model.entities.PaymentReceipt;
import com.example.spliteasybackend.receipts.domain.services.PaymentReceiptService;
import com.example.spliteasybackend.receipts.interfaces.rest.resources.PaymentReceiptResource;
import com.example.spliteasybackend.receipts.interfaces.rest.transform.PaymentReceiptResourceFromEntityAssembler;
import com.example.spliteasybackend.shared.infrastructure.storage.ObjectStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/payment-receipts", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Payment Receipts", description = "Upload and review member payment receipts")
public class PaymentReceiptsController {

    private final ObjectStorageService storageService;
    private final PaymentReceiptService receiptService;

    public PaymentReceiptsController(ObjectStorageService storageService,
                                     PaymentReceiptService receiptService) {
        this.storageService = storageService;
        this.receiptService = receiptService;
    }

    private Long currentUserId(HttpServletRequest req) {
        Object idAttr = req.getAttribute("userId");
        if (idAttr instanceof Number n) return n.longValue();
        throw new IllegalStateException("No se pudo determinar el ID del usuario autenticado (falta 'userId' en la request).");
    }

    private boolean isRepresentative(Authentication auth) {
        if (auth == null) return false;
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if ("ROLE_REPRESENTANTE".equals(ga.getAuthority())) return true;
        }
        return false;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_MIEMBRO','ROLE_REPRESENTANTE')")
    @Operation(summary = "Sube boleta de pago (queda EN_REVISION) para una member-contribution")
    public ResponseEntity<PaymentReceiptResource> uploadReceipt(
            @RequestParam("memberContributionId") Long memberContributionId,
            @RequestParam("file") MultipartFile file,
            Authentication auth,
            HttpServletRequest req
    ) {
        Long uploaderUserId = currentUserId(req);

        String keyPrefix = "receipts/mc-" + memberContributionId;

        String publicUrl = storageService.upload(file, keyPrefix);

        PaymentReceipt saved = receiptService.uploadReceipt(
                memberContributionId,
                uploaderUserId,
                file.getOriginalFilename(),
                publicUrl
        );

        var resource = PaymentReceiptResourceFromEntityAssembler.toResourceFromEntity(saved);
        return ResponseEntity
                .created(URI.create("/api/v1/payment-receipts/" + saved.getId()))
                .body(resource);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MIEMBRO','ROLE_REPRESENTANTE')")
    @Operation(summary = "Lista boletas de una member-contribution (dueño o representante)")
    public ResponseEntity<List<PaymentReceiptResource>> listReceipts(
            @RequestParam("memberContributionId") Long memberContributionId,
            Authentication auth,
            HttpServletRequest req
    ) {
        Long uid = currentUserId(req);
        boolean rep = isRepresentative(auth);

        var list = receiptService
                .listByMemberContribution(memberContributionId, uid, rep)
                .stream()
                .map(PaymentReceiptResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(list);
    }

    @PutMapping("/{receiptId}/approve")
    @PreAuthorize("hasAuthority('ROLE_REPRESENTANTE')")
    @Operation(summary = "Aprueba boleta (marca MemberContribution como PAGADO)")
    public ResponseEntity<PaymentReceiptResource> approve(
            @PathVariable Long receiptId,
            Authentication auth,
            HttpServletRequest req
    ) {
        Long reviewerUserId = currentUserId(req);

        return receiptService.approve(receiptId, reviewerUserId)
                .map(PaymentReceiptResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{receiptId}/reject")
    @PreAuthorize("hasAuthority('ROLE_REPRESENTANTE')")
    @Operation(summary = "Rechaza boleta (mantiene la contribución PENDIENTE)")
    public ResponseEntity<PaymentReceiptResource> reject(
            @PathVariable Long receiptId,
            @RequestParam(required = false) String notes,
            Authentication auth,
            HttpServletRequest req
    ) {
        Long reviewerUserId = currentUserId(req);

        return receiptService.reject(receiptId, reviewerUserId, notes)
                .map(PaymentReceiptResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
