package com.example.spliteasybackend.iam.interfaces.rest.transform;

import com.example.spliteasybackend.iam.domain.model.aggregates.User;
import com.example.spliteasybackend.iam.interfaces.rest.resources.AuthenticatedUserResource;

public class AuthenticatedUserResourceFromEntityAssembler {
    public static AuthenticatedUserResource toResourceFromEntity(User user, String token) {
        return new AuthenticatedUserResource(user.getId(), user.getUsername(), token);
    }
}
