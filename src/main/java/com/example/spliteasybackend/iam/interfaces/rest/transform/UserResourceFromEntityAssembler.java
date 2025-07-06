package com.example.spliteasybackend.iam.interfaces.rest.transform;

import com.example.spliteasybackend.iam.domain.model.aggregates.User;
import com.example.spliteasybackend.iam.domain.model.entities.Role;
import com.example.spliteasybackend.iam.interfaces.rest.resources.UserResource;

public class UserResourceFromEntityAssembler {
    public static UserResource toResourceFromEntity(User user) {
        var roles = user.getRoles().stream().map(Role::getStringName).toList();
        return new UserResource(user.getId(), user.getUsername(), user.getEmail(), user.getIncome(),roles);
    }
}
