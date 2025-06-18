// CreateMemberContributionCommand.java
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
) {}
