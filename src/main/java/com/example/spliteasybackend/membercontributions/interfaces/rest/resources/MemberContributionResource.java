package com.example.spliteasybackend.membercontributions.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MemberContributionResource(
        Long id,
        Long contributionId,
        Long memberId,
        BigDecimal monto,
        String status,
        LocalDateTime pagadoEn
) {
}
