package com.example.spliteasybackend.iam.interfaces.rest.resources;

import java.math.BigDecimal;
import java.util.List;

public record UserResource(
        Long id,
        String username,
        String email,
        BigDecimal income,
        List<String> roles
) {}

