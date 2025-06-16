package com.example.spliteasybackend.user.interfaces.acl;

public interface UsersContextFacade {

    Long createUser(
            String name,
            String email,
            String password,
            String role,
            double income
    );

    boolean existsUserById(Long id);

}
