package com.example.spliteasybackend.iam.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileResource(
        @NotBlank @Size(min = 2, max = 100) String username,
        @NotBlank @Email @Size(max = 100) String email
) {}
