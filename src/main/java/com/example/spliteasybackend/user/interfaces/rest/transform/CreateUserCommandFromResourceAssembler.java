package com.example.spliteasybackend.user.interfaces.rest.transform;

import com.example.spliteasybackend.user.domain.models.commands.CreateUserCommand;
import com.example.spliteasybackend.user.interfaces.rest.resources.CreateUserResource;

import com.example.spliteasybackend.user.domain.models.valueobjects.Role;

public class CreateUserCommandFromResourceAssembler {

    public static CreateUserCommand toCommandFromResource(CreateUserResource resource) {
        return new CreateUserCommand(
                resource.name(),
                resource.email(),
                resource.password(),
                Role.valueOf(resource.role().toUpperCase()),
                resource.income()
        );
    }
}

