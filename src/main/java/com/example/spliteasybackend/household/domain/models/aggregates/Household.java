package com.example.spliteasybackend.household.domain.models.aggregates;

import com.example.spliteasybackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Household extends AuditableAbstractAggregateRoot<Household> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    private String description;

    private String currency = "USD";

    @Column(name = "representante_id", nullable = false)
    private Long representanteId;

    public Household(String name, String description, String currency, Long representanteId) {
        this.name = name;
        this.description = description;
        this.currency = currency;
        this.representanteId = representanteId;
    }
}
