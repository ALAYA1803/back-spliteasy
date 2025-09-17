package com.example.spliteasybackend.receipts.interfaces.rest;

import com.example.spliteasybackend.receipts.domain.model.entities.PaymentReceipt;
import com.example.spliteasybackend.receipts.domain.services.PaymentReceiptService;
import com.example.spliteasybackend.receipts.interfaces.rest.resources.PaymentReceiptResource;
import com.example.spliteasybackend.receipts.interfaces.rest.transform.PaymentReceiptResourceFromEntityAssembler;
import com.example.spliteasybackend.shared.domain.services.FileStorageService;

import com.example.spliteasybackend.iam.domain.model.aggregates.User;
import com.example.spliteasybackend.iam.infrastructure.persistence.jpa.repositories.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * Resuelve el ID del usuario autenticado SIN usar oauth2:
     * 1) Atributo "userId" del request (si algún filtro lo setea)
     * 2) Username desde Authentication -> lookup en BD
     * Si falla, lanza 401 (no 500).
     */
    private Long currentUserId(Authentication auth, HttpServletRequest req) {
        Object idAttr = req.getAttribute("userId");
        if (idAttr instanceof Number) return ((Number) idAttr).longValue();
        if (idAttr instanceof String) {
            try { return Long.parseLong((String) idAttr); }
            catch (NumberFormatException ignore) {}
        }

        if (auth == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }

        String username = null;
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        }
        if (username == null || username.isBlank()) {
            username = auth.getName();
        }
        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no identificado");
        }

        final String uname = username;
        return userRepository.findByUsername(uname)
                .map(User::getId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Usuario autenticado no encontrado: " + uname));

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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Archivo vacío");
        }
        if (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Nombre de archivo inválido");
        }

        final Long uid = currentUserId(auth, req);
        final String publicUrl = storageService.store(file);

        PaymentReceipt saved = receiptService.uploadReceipt(
                memberContributionId, uid, file.getOriginalFilename(), publicUrl
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
        final Long uid = currentUserId(auth, req);
        final boolean rep = isRepresentative(auth);

        List<PaymentReceiptResource> list = receiptService
                .listByMemberContribution(memberContributionId, uid, rep)
                .stream()
                .map(PaymentReceiptResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }

    @PostMapping("/receipts/{receiptId}/approve")
    @PreAuthorize("hasAuthority('ROLE_REPRESENTANTE')")
    @Operation(summary = "Aprueba boleta (marca la MemberContribution como PAGADO si corresponde)")
    public ResponseEntity<PaymentReceiptResource> approve(@PathVariable Long receiptId,
                                                          Authentication auth,
                                                          HttpServletRequest req) {
        final Long reviewerId = currentUserId(auth, req);
        try {
            return receiptService.approve(receiptId, reviewerId)
                    .map(PaymentReceiptResourceFromEntityAssembler::toResourceFromEntity)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        }
    }

    @PostMapping("/receipts/{receiptId}/reject")
    @PreAuthorize("hasAuthority('ROLE_REPRESENTANTE')")
    @Operation(summary = "Rechaza boleta (mantiene la contribución PENDIENTE)")
    public ResponseEntity<PaymentReceiptResource> reject(@PathVariable Long receiptId,
                                                         @RequestParam(required = false) String notes,
                                                         Authentication auth,
                                                         HttpServletRequest req) {
        final Long reviewerId = currentUserId(auth, req);
        try {
            return receiptService.reject(receiptId, reviewerId, notes)
                    .map(PaymentReceiptResourceFromEntityAssembler::toResourceFromEntity)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        }
    }
}
