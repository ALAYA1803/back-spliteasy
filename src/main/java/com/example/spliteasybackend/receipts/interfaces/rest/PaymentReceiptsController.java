package com.example.spliteasybackend.receipts.interfaces.rest;

import com.example.spliteasybackend.receipts.domain.model.entities.PaymentReceipt;
import com.example.spliteasybackend.receipts.domain.services.PaymentReceiptService;
import com.example.spliteasybackend.receipts.interfaces.rest.resources.PaymentReceiptResource;
import com.example.spliteasybackend.receipts.interfaces.rest.transform.PaymentReceiptResourceFromEntityAssembler;
import com.example.spliteasybackend.shared.domain.services.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.Objects;


import com.example.spliteasybackend.iam.domain.model.aggregates.User ;
import com.example.spliteasybackend.iam.infrastructure.persistence.jpa.repositories.UserRepository ;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Payment Receipts", description = "Upload and review member payment receipts")
public class PaymentReceiptsController {

    private final FileStorageService storageService;
    private final PaymentReceiptService receiptService;
    private final UserRepository userRepository;

    public PaymentReceiptsController(FileStorageService storageService,
                                     PaymentReceiptService receiptService,
                                     UserRepository userRepository) {
        this.storageService = storageService;
        this.receiptService = receiptService;
        this.userRepository = userRepository;
    }

    private Long currentUserId(Authentication auth, HttpServletRequest req) {
        Object idAttr = req.getAttribute("userId");
        if (idAttr instanceof Number n) return n.longValue();

        String username = auth != null ? auth.getName() : null;
        if (username == null || username.isBlank()) {
            throw new IllegalStateException("No se pudo determinar el usuario autenticado (auth vacío).");
        }

        return userRepository.findByUsername(username)
                .map(User::getId)
                .orElseThrow(() -> new IllegalStateException(
                        "No se pudo determinar el ID del usuario (username=" + username + ")."));
    }

    private boolean isRepresentative(Authentication auth) {
        if (auth == null || auth.getAuthorities() == null) return false;
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if ("ROLE_REPRESENTANTE".equals(ga.getAuthority())) return true;
        }
        return false;
    }

    @PostMapping(
            value = "/member-contributions/{memberContributionId}/receipts",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Sube boleta de pago (queda PENDING) para una member-contribution")
    public ResponseEntity<PaymentReceiptResource> uploadReceipt(@PathVariable Long memberContributionId,
                                                                @RequestPart("file") MultipartFile file,
                                                                Authentication auth,
                                                                HttpServletRequest req) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }

        Long uid = currentUserId(auth, req);
        String publicUrl = storageService.store(file);
        PaymentReceipt saved = receiptService.uploadReceipt(
                memberContributionId,
                uid,
                file.getOriginalFilename(),
                publicUrl
        );

        PaymentReceiptResource body = PaymentReceiptResourceFromEntityAssembler.toResourceFromEntity(saved);

        URI location = URI.create(String.format("/api/v1/member-contributions/%d/receipts", memberContributionId));
        return ResponseEntity.created(location).body(body);
    }

    @GetMapping("/member-contributions/{memberContributionId}/receipts")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Lista boletas de una member-contribution (miembro dueño o representante)")
    public ResponseEntity<List<PaymentReceiptResource>> listReceipts(@PathVariable Long memberContributionId,
                                                                     Authentication auth,
                                                                     HttpServletRequest req) {
        Long uid = currentUserId(auth, req);
        boolean rep = isRepresentative(auth);

        var list = receiptService
                .listByMemberContribution(memberContributionId, uid, rep)
                .stream()
                .map(PaymentReceiptResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(list);
    }

    @PostMapping("/receipts/{receiptId}/approve")
    @PreAuthorize("hasAuthority('ROLE_REPRESENTANTE')")
    @Operation(summary = "Aprueba boleta (marca la MemberContribution como PAGADO si corresponde)")
    public ResponseEntity<PaymentReceiptResource> approve(@PathVariable Long receiptId,
                                                          Authentication auth,
                                                          HttpServletRequest req) {
        Long reviewerId = currentUserId(auth, req);

        return receiptService.approve(receiptId, reviewerId)
                .map(PaymentReceiptResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/receipts/{receiptId}/reject")
    @PreAuthorize("hasAuthority('ROLE_REPRESENTANTE')")
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
