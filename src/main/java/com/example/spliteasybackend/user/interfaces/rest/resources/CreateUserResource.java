package com.example.spliteasybackend.user.interfaces.rest.resources;

import java.math.BigDecimal;

public record CreateUserResource(
        String name,
        String email,
        String password,
        String role,
        BigDecimal income
) {

    public CreateUserResource {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name cannot be blank");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("email cannot be blank");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("password cannot be blank");
        if (role == null || role.isBlank()) throw new IllegalArgumentException("role cannot be blank");
        if (income == null || income.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("income must be zero or positive");
    }
}
