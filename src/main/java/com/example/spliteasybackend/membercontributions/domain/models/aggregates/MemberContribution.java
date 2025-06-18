package com.example.spliteasybackend.membercontributions.domain.models.aggregates;

import com.example.spliteasybackend.membercontributions.domain.models.commands.CreateMemberContributionCommand;
import com.example.spliteasybackend.membercontributions.domain.models.valueobjects.Status;
import com.example.spliteasybackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
public class MemberContribution extends AuditableAbstractAggregateRoot<MemberContribution> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long contributionId;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @Column
    private LocalDateTime pagadoEn;

    public MemberContribution(CreateMemberContributionCommand command) {
        this.contributionId = command.contributionId();
        this.memberId = command.memberId();
        this.monto = command.monto() != null ? command.monto() : BigDecimal.ZERO;
        this.status = command.status();
        this.pagadoEn = command.pagadoEn();
    }

    public MemberContribution() {
        // Constructor requerido por JPA
    }

    public void update(CreateMemberContributionCommand command) {
        this.contributionId = command.contributionId();
        this.memberId = command.memberId();
        this.monto = command.monto() != null ? command.monto() : BigDecimal.ZERO;
        this.status = command.status();
        this.pagadoEn = command.pagadoEn();
    }
}
