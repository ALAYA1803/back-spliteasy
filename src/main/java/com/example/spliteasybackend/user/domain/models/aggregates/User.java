package com.example.spliteasybackend.user.domain.models.aggregates;

import com.example.spliteasybackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.example.spliteasybackend.user.domain.models.commands.CreateUserCommand;
import com.example.spliteasybackend.user.domain.models.valueobjects.EmailAddress;
import com.example.spliteasybackend.user.domain.models.valueobjects.Role;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Getter
public class User extends AuditableAbstractAggregateRoot<User> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Embedded
    private EmailAddress email;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal income;

    // Constructor de dominio
    public User(CreateUserCommand command) {
        this.name = command.name();
        this.email = new EmailAddress(command.email());
        this.password = command.password();
        this.role = command.role();
        this.income = command.income() != null ? command.income() : BigDecimal.ZERO;
    }

    // Constructor requerido por JPA
    public User() {}

    // Método de fábrica con validación de negocio
    public static User crear(CreateUserCommand command) {
        if (command.income() != null && command.income().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El ingreso no puede ser negativo");
        }
        return new User(command);
    }

    // Actualiza los datos del usuario con validación
    public void update(CreateUserCommand command) {
        this.name = command.name();
        this.email = new EmailAddress(command.email());
        this.password = command.password();
        this.role = command.role();
        this.income = command.income() != null ? command.income() : BigDecimal.ZERO;
    }

    // Verifica si el usuario es representante
    public boolean isRepresentante() {
        return this.role == Role.REPRESENTANTE;
    }

    // Verifica si el usuario es miembro
    public boolean isMiembro() {
        return this.role == Role.MIEMBRO;
    }

    // Cambia el ingreso con validación de negocio
    public void actualizarIngreso(BigDecimal nuevoIngreso) {
        if (nuevoIngreso == null || nuevoIngreso.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El ingreso no puede ser negativo");
        }
        this.income = nuevoIngreso.setScale(2, RoundingMode.HALF_UP);
    }

    // Cambia el rol con posible lógica de dominio futuro
    public void cambiarRol(Role nuevoRol) {
        if (this.role == Role.REPRESENTANTE && nuevoRol == Role.MIEMBRO) {
            // Validación avanzada si representa hogares (se implementará después)
            throw new IllegalStateException("No se puede cambiar a MIEMBRO si ya es representante de un hogar.");
        }
        this.role = nuevoRol;
    }
}
