package com.example.spliteasybackend.iam.domain.model.aggregates;

import com.example.spliteasybackend.iam.domain.model.entities.Role;
import com.example.spliteasybackend.iam.domain.model.valueobjects.Roles;
import com.example.spliteasybackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User aggregate root
 * This class represents the aggregate root for the User entity.
 *
 * @see AuditableAbstractAggregateRoot
 */
@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends AuditableAbstractAggregateRoot<User> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String username;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(unique = true)
    private String email;

    @NotBlank
    @Size(max = 255)
    private String password;

    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal income = BigDecimal.ZERO;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    public User() {
        this.roles = new HashSet<>();
    }

    public User(String username, String email, String password, BigDecimal income, List<Role> roles) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.income = income != null ? income : BigDecimal.ZERO;
        this.roles = new HashSet<>();
        addRoles(roles);
    }

    public User addRole(Role role) {
        this.roles.add(role);
        return this;
    }

    public User addRoles(List<Role> roles) {
        var validatedRoleSet = Role.validateRoleSet(roles);
        this.roles.addAll(validatedRoleSet);
        return this;
    }

    // Verifica si el usuario tiene el rol REPRESENTANTE
    public boolean isRepresentante() {
        return roles.stream()
                .anyMatch(role -> role.getName() == Roles.ROLE_REPRESENTANTE);
    }

    // Verifica si el usuario tiene el rol MIEMBRO
    public boolean isMiembro() {
        return roles.stream()
                .anyMatch(role -> role.getName() == Roles.ROLE_MIEMBRO);
    }
}
