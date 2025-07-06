package com.example.spliteasybackend.membercontributions.domain.models.commands;

import com.example.spliteasybackend.membercontributions.domain.models.valueobjects.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateMemberContributionCommand(
        Long contributionId,
        Long memberId,
        BigDecimal monto,
        Status status,
        LocalDateTime pagadoEn
) {
    public CreateMemberContributionCommand {
        if (contributionId == null || contributionId <= 0) {
            throw new IllegalArgumentException("El ID de la contribución es obligatorio y debe ser positivo");
        }

        if (memberId == null || memberId <= 0) {
            throw new IllegalArgumentException("El ID del miembro es obligatorio y debe ser positivo");
        }

        if (monto == null || monto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El monto no puede ser nulo ni negativo");
        }

        if (status == null) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }

        // Validación opcional (solo si status == PAGADO, se espera pagadoEn no nulo)
        if (status == Status.PAGADO && pagadoEn == null) {
            throw new IllegalArgumentException("Debe especificar la fecha de pago cuando el estado es PAGADO");
        }
    }
}
