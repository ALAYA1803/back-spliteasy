package com.example.spliteasybackend.membercontributions.domain.models.aggregates;

import com.example.spliteasybackend.contributions.domain.models.aggregates.Contribution;
import com.example.spliteasybackend.membercontributions.domain.models.commands.CreateMemberContributionCommand;
import com.example.spliteasybackend.membercontributions.domain.models.valueobjects.Status;
import com.example.spliteasybackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.example.spliteasybackend.iam.domain.model.aggregates.User;
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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "contribution_id", nullable = false)
    private Contribution contribution;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private User member;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @Column
    private LocalDateTime pagadoEn;

    protected MemberContribution() {}

    public MemberContribution(Contribution contribution, User member, BigDecimal monto) {
        this.contribution = contribution;
        this.member = member;
        this.monto = monto;
        this.status = Status.PENDIENTE;
        this.pagadoEn = null;
    }

    public void update(CreateMemberContributionCommand command) {
        this.monto = command.monto();
        this.status = command.status();
        this.pagadoEn = command.pagadoEn();
    }

    public Long getContributionId() {
        return (contribution != null) ? contribution.getId() : null;
    }

    public Long getMemberId() {
        return (member != null) ? member.getId() : null;
    }
}
