package com.example.spliteasybackend.bills.domain.models.aggregates;

import com.example.spliteasybackend.bills.domain.models.commands.CreateBillCommand;
import com.example.spliteasybackend.bills.domain.models.valueobjects.Money;
import com.example.spliteasybackend.households.domain.models.aggregates.Household;
import com.example.spliteasybackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.example.spliteasybackend.user.domain.models.aggregates.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
public class Bill extends AuditableAbstractAggregateRoot<Bill> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "household_id", nullable = false)
    private Household household;

    @Column(nullable = false, length = 255)
    private String description;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "monto", nullable = false))
    private Money monto;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(nullable = false)
    private LocalDate fecha;

    protected Bill() {
        // Requerido por JPA
    }

    private Bill(Household household, String description, Money monto, User createdBy, LocalDate fecha) {
        this.household = household;
        this.description = description;
        this.monto = monto;
        this.createdBy = createdBy;
        this.fecha = fecha;
    }

    public static Bill create(CreateBillCommand command, Household household, User creator) {
        if (command.monto() == null || command.monto().value().compareTo(Money.ZERO.value()) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero");
        }

        if (command.description() == null || command.description().isBlank()) {
            throw new IllegalArgumentException("Descripción no puede ser vacía");
        }

        if (command.fecha() == null) {
            throw new IllegalArgumentException("Fecha no puede ser nula");
        }

        return new Bill(household, command.description(), command.monto(), creator, command.fecha());
    }

    public void update(CreateBillCommand command, Household household, User creator) {
        this.household = household;
        this.description = command.description();
        this.monto = command.monto();
        this.createdBy = creator;
        this.fecha = command.fecha();
    }
}
