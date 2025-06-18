package com.example.spliteasybackend.households.domain.models.aggregates;

import com.example.spliteasybackend.households.domain.models.commands.CreateHouseholdCommand;
import com.example.spliteasybackend.households.domain.models.valueobjects.Name;
import com.example.spliteasybackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Household extends AuditableAbstractAggregateRoot<Household> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Name name;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false, length = 10)
    private String currency;

    @Column(nullable = false)
    private Long representanteId;

    public Household(CreateHouseholdCommand command) {
        this.name = new Name(command.name());
        this.description = command.description();
        this.currency = command.currency();
        this.representanteId = command.representanteId();
    }

    public Household() {
        // Constructor requerido por JPA
    }

    public void update(CreateHouseholdCommand command) {
        this.name = new Name(command.name());
        this.description = command.description();
        this.currency = command.currency();
        this.representanteId = command.representanteId();
    }
}
