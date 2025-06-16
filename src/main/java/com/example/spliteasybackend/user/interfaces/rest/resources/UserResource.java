package com.example.spliteasybackend.user.interfaces.rest.resources;

import java.math.BigDecimal;

public record UserResource(
        Long id,
        String name,
        String email,
        String role,
        BigDecimal income
) {
}
