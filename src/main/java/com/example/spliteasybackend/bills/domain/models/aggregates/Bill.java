package com.example.spliteasybackend.bills.domain.models.aggregates;

import com.example.spliteasybackend.bills.domain.models.commands.CreateBillCommand;
import com.example.spliteasybackend.bills.domain.models.valueobjects.Money;
import com.example.spliteasybackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
public class Bill extends AuditableAbstractAggregateRoot<Bill> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long householdId;

    @Column(nullable = false, length = 255)
    private String description;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "monto", nullable = false))
    private Money monto;

    @Column(nullable = false)
    private Long createdBy;

    @Column(nullable = false)
    private LocalDate fecha;

    public Bill(CreateBillCommand command) {
        if (command.monto() == null)
            throw new IllegalArgumentException("El monto no puede ser nulo");

        this.householdId = command.householdId();
        this.description = command.description();
        this.monto = command.monto();
        this.createdBy = command.createdBy();
        this.fecha = command.fecha();
    }


    public Bill() {
        // Constructor por defecto requerido por JPA
    }

    public void update(CreateBillCommand command) {
        this.householdId = command.householdId();
        this.description = command.description();
        this.monto = command.monto();
        this.createdBy = command.createdBy();
        this.fecha = command.fecha();
    }
}
