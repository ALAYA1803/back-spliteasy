package com.example.spliteasybackend.user.interfaces.rest.transform;

import com.example.spliteasybackend.user.domain.models.aggregates.User;
import com.example.spliteasybackend.user.interfaces.rest.resources.UserResource;

public class UserResourceFromEntityAssembler {

    public static UserResource toResourceFromEntity(User entity) {
        return new UserResource(
                entity.getId(),
                entity.getName(),
                entity.getEmail().address(),
                entity.getRole().name(),         // Convertir enum a String
                entity.getIncome()               // Ya es BigDecimal
        );
    }

}
