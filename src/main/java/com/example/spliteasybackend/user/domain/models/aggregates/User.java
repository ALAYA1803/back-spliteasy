package com.example.spliteasybackend.user.domain.models.aggregates;

import com.example.spliteasybackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.example.spliteasybackend.user.domain.models.commands.CreateUserCommand;
import com.example.spliteasybackend.user.domain.models.valueobjects.Role;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
@Getter
public class User extends AuditableAbstractAggregateRoot<User> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 👈 agregado aquí

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal income;

    public User(CreateUserCommand command) {
        this.name = command.name();
        this.email = command.email();
        this.password = command.password();
        this.role = command.role();
        this.income = command.income() != null ? command.income() : BigDecimal.ZERO;
    }

    public User() {
        // Constructor por defecto requerido por JPA
    }

    public void update(CreateUserCommand command) {
        this.name = command.name();
        this.email = command.email();
        this.password = command.password();
        this.role = command.role();
        this.income = command.income() != null ? command.income() : BigDecimal.ZERO;
    }
}
