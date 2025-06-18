package com.example.spliteasybackend.contributions.domain.models.aggregates;

import com.example.spliteasybackend.contributions.domain.models.commands.CreateContributionCommand;
import com.example.spliteasybackend.contributions.domain.models.valueobjects.Strategy;
import com.example.spliteasybackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
public class Contribution extends AuditableAbstractAggregateRoot<Contribution> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long billId;

    @Column(nullable = false)
    private Long householdId;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false)
    private LocalDate fechaLimite;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Strategy strategy;

    public Contribution(CreateContributionCommand command) {
        this.billId = command.billId();
        this.householdId = command.householdId();
        this.description = command.description();
        this.fechaLimite = command.fechaLimite();
        this.strategy = command.strategy();
    }

    public Contribution() {
        // Constructor requerido por JPA
    }

    public void update(CreateContributionCommand command) {
        this.billId = command.billId();
        this.householdId = command.householdId();
        this.description = command.description();
        this.fechaLimite = command.fechaLimite();
        this.strategy = command.strategy();
    }
}
