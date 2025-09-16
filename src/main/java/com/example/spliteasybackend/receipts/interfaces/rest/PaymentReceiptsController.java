package com.example.spliteasybackend.receipts.interfaces.rest;

import com.example.spliteasybackend.receipts.domain.model.entities.PaymentReceipt;
import com.example.spliteasybackend.receipts.domain.services.PaymentReceiptService;
import com.example.spliteasybackend.receipts.interfaces.rest.resources.PaymentReceiptResource;
import com.example.spliteasybackend.receipts.interfaces.rest.transform.PaymentReceiptResourceFromEntityAssembler;
import com.example.spliteasybackend.shared.domain.services.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Tag(name = "Payment Receipts", description = "Upload and review member payment receipts")
public class PaymentReceiptsController {

    private final FileStorageService storageService;
    private final PaymentReceiptService receiptService;

    public PaymentReceiptsController(FileStorageService storageService,
                                     PaymentReceiptService receiptService) {
        this.storageService = storageService;
        this.receiptService = receiptService;
    }

    private Long currentUserId(Authentication auth, HttpServletRequest req) {
        Object idAttr = req.getAttribute("userId");
        if (idAttr instanceof Number n) return n.longValue();

        // fallback: si solo tenemos username (puesto en el filtro)
        Object uAttr = req.getAttribute("username");
        if (uAttr != null) {
            // Aquí podrías resolver el ID por username usando tu UserRepository si lo necesitas.
            // throw new IllegalStateException("No se pudo determinar el ID del usuario");
            // Por simplicidad:
            throw new IllegalStateException("No se pudo determinar el ID del usuario autenticado (falta userId en la request).");
        }
        throw new IllegalStateException("No se pudo determinar el ID del usuario autenticado");
    }

    private boolean isRepresentative(Authentication auth) {
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if ("ROLE_REPRESENTANTE".equals(ga.getAuthority())) return true;
        }
        return false;
    }

    @PostMapping(value = "/api/v1/member-contributions/{memberContributionId}/receipts",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Sube boleta de pago (queda PENDING) para una member-contribution")
    public ResponseEntity<PaymentReceiptResource> uploadReceipt(@PathVariable Long memberContributionId,
                                                                @RequestPart("file") MultipartFile file,
                                                                Authentication auth,
                                                                HttpServletRequest req) {
        Long uid = currentUserId(auth, req);
        String publicUrl = storageService.store(file);
        PaymentReceipt saved = receiptService.uploadReceipt(memberContributionId, uid, file.getOriginalFilename(), publicUrl);
        return ResponseEntity.ok(PaymentReceiptResourceFromEntityAssembler.toResourceFromEntity(saved));
    }

    @GetMapping("/api/v1/member-contributions/{memberContributionId}/receipts")
    @Operation(summary = "Lista boletas de una member-contribution (dueño o representante)")
    public ResponseEntity<List<PaymentReceiptResource>> listReceipts(@PathVariable Long memberContributionId,
                                                                     Authentication auth,
                                                                     HttpServletRequest req) {
        Long uid = currentUserId(auth, req);
        boolean rep = isRepresentative(auth);
        var list = receiptService.listByMemberContribution(memberContributionId, uid, rep)
                .stream().map(PaymentReceiptResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/api/v1/receipts/{receiptId}/approve")
    @Operation(summary = "Aprueba boleta (marca MemberContribution como PAGADO)")
    public ResponseEntity<PaymentReceiptResource> approve(@PathVariable Long receiptId,
                                                          Authentication auth,
                                                          HttpServletRequest req) {
        Long reviewerId = currentUserId(auth, req);
        return receiptService.approve(receiptId, reviewerId)
                .map(PaymentReceiptResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/api/v1/receipts/{receiptId}/reject")
    @Operation(summary = "Rechaza boleta (mantiene la contribución PENDIENTE)")
    public ResponseEntity<PaymentReceiptResource> reject(@PathVariable Long receiptId,
                                                         @RequestParam(required = false) String notes,
                                                         Authentication auth,
                                                         HttpServletRequest req) {
        Long reviewerId = currentUserId(auth, req);
        return receiptService.reject(receiptId, reviewerId, notes)
                .map(PaymentReceiptResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
