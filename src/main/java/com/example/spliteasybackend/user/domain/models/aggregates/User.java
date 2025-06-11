package com.example.spliteasybackend.user.domain.models.aggregates;

import com.example.spliteasybackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class User extends AuditableAbstractAggregateRoot<User> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    private BigDecimal income = BigDecimal.ZERO;

    public enum Role {
        REPRESENTANTE,
        MIEMBRO
    }

    public User(String name, String email, String password, Role role, BigDecimal income) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.income = income;
    }
}