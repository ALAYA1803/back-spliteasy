package com.example.spliteasybackend.iam.interfaces.rest.resources;

public record AccountProfileResource(
        Long id,
        String username,
        String email
) {}
