package com.example.spliteasybackend.contributions.domain.models.aggregates;

import com.example.spliteasybackend.bills.domain.models.aggregates.Bill;
import com.example.spliteasybackend.contributions.domain.models.commands.CreateContributionCommand;
import com.example.spliteasybackend.contributions.domain.models.valueobjects.Strategy;
import com.example.spliteasybackend.householdmembers.domain.models.aggregates.HouseholdMember;
import com.example.spliteasybackend.households.domain.models.aggregates.Household;
import com.example.spliteasybackend.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.example.spliteasybackend.membercontributions.domain.models.aggregates.MemberContribution;
import com.example.spliteasybackend.membercontributions.infrastructure.persistance.jpa.repositories.MemberContributionRepository;
import com.example.spliteasybackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.example.spliteasybackend.iam.domain.model.aggregates.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
public class Contribution extends AuditableAbstractAggregateRoot<Contribution> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id", nullable = false)
    private Bill bill;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "household_id", nullable = false)
    private Household household;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(name = "fecha_limite", nullable = false)
    private LocalDate fechaLimite;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Strategy strategy;

    protected Contribution() {
        // Requerido por JPA
    }

    private Contribution(Bill bill, Household household, String description, LocalDate fechaLimite, Strategy strategy) {
        this.bill = bill;
        this.household = household;
        this.description = description;
        this.fechaLimite = fechaLimite;
        this.strategy = strategy;
    }

    public static Contribution create(CreateContributionCommand command, Bill bill, Household household) {
        if (!bill.getHousehold().getId().equals(household.getId())) {
            throw new IllegalArgumentException("El bill no pertenece al household indicado.");
        }

        return new Contribution(
                bill,
                household,
                command.description(),
                command.fechaLimite(),
                command.strategy()
        );
    }

    public void update(CreateContributionCommand command, Bill bill, Household household) {
        if (!bill.getHousehold().getId().equals(household.getId())) {
            throw new IllegalArgumentException("El bill no pertenece al household indicado.");
        }

        this.bill = bill;
        this.household = household;
        this.description = command.description();
        this.fechaLimite = command.fechaLimite();
        this.strategy = command.strategy();
    }

    public void distribute(
            List<HouseholdMember> members,
            UserRepository userRepository,
            MemberContributionRepository memberContributionRepository) {

        if (members == null || members.isEmpty()) {
            throw new IllegalArgumentException("No hay miembros en el hogar.");
        }

        var totalAmount = this.bill.getMonto().value();

        switch (this.strategy) {
            case EQUAL -> {
                BigDecimal equalAmount = totalAmount
                        .divide(BigDecimal.valueOf(members.size()), 2, RoundingMode.HALF_UP);
                for (var member : members) {
                    var user = userRepository.findById(member.getUser().getId())
                            .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
                    memberContributionRepository.save(new MemberContribution(this, user, equalAmount));
                }
            }

            case INCOME_BASED -> {
                BigDecimal totalIncome = members.stream()
                        .map(member -> userRepository.findById(member.getUser().getId())
                                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"))
                                .getIncome())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                if (totalIncome.compareTo(BigDecimal.ZERO) == 0) {
                    throw new IllegalStateException("La suma total de ingresos es cero. No se puede distribuir.");
                }

                for (var member : members) {
                    User user = userRepository.findById(member.getUser().getId())
                            .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));

                    BigDecimal porcentaje = user.getIncome()
                            .divide(totalIncome, 5, RoundingMode.HALF_UP);
                    BigDecimal montoAsignado = totalAmount.multiply(porcentaje)
                            .setScale(2, RoundingMode.HALF_UP);

                    memberContributionRepository.save(new MemberContribution(this, user, montoAsignado));
                }
            }

            default -> throw new UnsupportedOperationException("Estrategia no soportada: " + this.strategy);
        }
    }

}
