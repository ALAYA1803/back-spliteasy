package com.example.spliteasybackend.user.domain.models.commands;

import com.example.spliteasybackend.user.domain.models.valueobjects.Role;

import java.math.BigDecimal;

/*
 * Create User Command
 */
public record CreateUserCommand(
        String name,
        String email,
        String password,
        Role role,
        BigDecimal income
) {}
