package com.example.spliteasybackend.households.domain.models.aggregates;

import com.example.spliteasybackend.households.domain.models.commands.CreateHouseholdCommand;
import com.example.spliteasybackend.households.domain.models.valueobjects.Name;
import com.example.spliteasybackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.example.spliteasybackend.user.domain.models.aggregates.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.Set;

@Entity
@Getter
public class Household extends AuditableAbstractAggregateRoot<Household> {

    private static final Set<String> SUPPORTED_CURRENCIES = Set.of("USD", "PEN", "EUR", "MXN");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Name name;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false, length = 10)
    private String currency;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "representante_id", nullable = false)
    private User representante;

    // Constructor protegido para JPA
    protected Household() {}

    // Método de fábrica con validaciones
    public static Household crear(CreateHouseholdCommand command, User representante) {
        if (command.name() == null || command.name().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del hogar no puede estar vacío");
        }

        if (!SUPPORTED_CURRENCIES.contains(command.currency())) {
            throw new IllegalArgumentException("Moneda no soportada: " + command.currency());
        }

        if (!representante.isRepresentante()) {
            throw new IllegalArgumentException("El usuario debe tener rol REPRESENTANTE");
        }

        return new Household(command, representante);
    }

    // Constructor privado para control de creación
    private Household(CreateHouseholdCommand command, User representante) {
        this.name = new Name(command.name());
        this.description = command.description();
        this.currency = command.currency();
        this.representante = representante;
    }

    // Lógica de actualización controlada
    // Para actualizar datos normales del hogar
    public void update(CreateHouseholdCommand command) {
        this.name = new Name(command.name());
        this.description = command.description();
        this.currency = command.currency();
    }

    // Para cambiar el representante con su lógica propia
    public void transferirRepresentacionA(User nuevoRepresentante) {
        if (!nuevoRepresentante.isRepresentante()) {
            throw new IllegalArgumentException("El nuevo representante debe tener rol REPRESENTANTE");
        }
        this.representante = nuevoRepresentante;
    }
}
