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
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payment-receipts")
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

        throw new IllegalStateException("No se pudo determinar el ID del usuario autenticado (falta 'userId' en la request).");
    }

    private boolean isRepresentative(Authentication auth) {
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if ("ROLE_REPRESENTANTE".equals(ga.getAuthority())) return true;
        }
        return false;
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Sube boleta de pago (queda EN_REVISION) para una member-contribution")
    public ResponseEntity<?> uploadReceipt(@RequestParam("memberContributionId") Long memberContributionId,
                                           @RequestPart("file") MultipartFile file,
                                           Authentication auth,
                                           HttpServletRequest req) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("Falta el archivo 'file'.");
            }
            Long uid = currentUserId(auth, req);
            String publicUrl = storageService.store(file);

            PaymentReceipt saved = receiptService.uploadReceipt(
                    memberContributionId, uid, file.getOriginalFilename(), publicUrl);

            return ResponseEntity.ok(PaymentReceiptResourceFromEntityAssembler.toResourceFromEntity(saved));
        } catch (MaxUploadSizeExceededException ex) {
            return ResponseEntity.status(413).body("El archivo es demasiado grande.");
        } catch (SecurityException se) {
            return ResponseEntity.status(403).body(se.getMessage());
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.badRequest().body(iae.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al subir la boleta.");
        }
    }

    @GetMapping
    @Operation(summary = "Lista boletas de una member-contribution (dueño o representante)")
    public ResponseEntity<?> listReceipts(@RequestParam("memberContributionId") Long memberContributionId,
                                          Authentication auth,
                                          HttpServletRequest req) {
        try {
            Long uid = currentUserId(auth, req);
            boolean rep = isRepresentative(auth);
            var list = receiptService.listByMemberContribution(memberContributionId, uid, rep)
                    .stream().map(PaymentReceiptResourceFromEntityAssembler::toResourceFromEntity).toList();
            return ResponseEntity.ok(list);
        } catch (SecurityException se) {
            return ResponseEntity.status(403).body(se.getMessage());
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.badRequest().body(iae.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al listar boletas.");
        }
    }

    @PutMapping("/{receiptId}/approve")
    @Operation(summary = "Aprueba boleta (marca MemberContribution como PAGADO)")
    public ResponseEntity<?> approve(@PathVariable Long receiptId,
                                     Authentication auth,
                                     HttpServletRequest req) {
        try {
            Long reviewerId = currentUserId(auth, req);
            var opt = receiptService.approve(receiptId, reviewerId);
            return opt
                    .map(PaymentReceiptResourceFromEntityAssembler::toResourceFromEntity)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (SecurityException se) {
            return ResponseEntity.status(403).body(se.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al aprobar boleta.");
        }
    }

    @PutMapping("/{receiptId}/reject")
    @Operation(summary = "Rechaza boleta (mantiene la contribución PENDIENTE)")
    public ResponseEntity<?> reject(@PathVariable Long receiptId,
                                    @RequestParam(required = false) String notes,
                                    Authentication auth,
                                    HttpServletRequest req) {
        try {
            Long reviewerId = currentUserId(auth, req);
            var opt = receiptService.reject(receiptId, reviewerId, notes);
            return opt
                    .map(PaymentReceiptResourceFromEntityAssembler::toResourceFromEntity)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (SecurityException se) {
            return ResponseEntity.status(403).body(se.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al rechazar boleta.");
        }
    }
}
