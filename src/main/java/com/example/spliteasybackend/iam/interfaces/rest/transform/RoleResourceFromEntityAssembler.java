package com.example.spliteasybackend.iam.interfaces.rest.transform;

import com.example.spliteasybackend.iam.domain.model.entities.Role;
import com.example.spliteasybackend.iam.interfaces.rest.resources.RoleResource;

public class RoleResourceFromEntityAssembler {
    public static RoleResource toResourceFromEntity(Role role) {
        return new RoleResource(role.getId(), role.getStringName());
    }
}
