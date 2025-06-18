package com.example.spliteasybackend.membercontributions.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateMemberContributionResource(
        Long contributionId,
        Long memberId,
        BigDecimal monto,
        String status,
        LocalDateTime pagadoEn
) {
    public CreateMemberContributionResource {
        if (contributionId == null || contributionId <= 0)
            throw new IllegalArgumentException("contributionId must be a positive number");

        if (memberId == null || memberId <= 0)
            throw new IllegalArgumentException("memberId must be a positive number");

        if (monto == null || monto.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("monto must be zero or positive");

        if (status == null || status.isBlank())
            throw new IllegalArgumentException("status cannot be blank");

        // pagadoEn puede ser null si aún no ha pagado — no se valida como obligatorio
    }
}
