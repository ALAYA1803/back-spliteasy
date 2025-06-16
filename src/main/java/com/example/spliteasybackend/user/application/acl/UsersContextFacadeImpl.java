// UsersContextFacadeImpl.java
package com.example.spliteasybackend.user.application.acl;

import com.example.spliteasybackend.user.domain.models.commands.CreateUserCommand;
import com.example.spliteasybackend.user.domain.models.queries.GetUserByIdQuery;
import com.example.spliteasybackend.user.domain.models.valueobjects.Role;
import com.example.spliteasybackend.user.domain.services.UserCommandService;
import com.example.spliteasybackend.user.domain.services.UserQueryService;
import com.example.spliteasybackend.user.interfaces.acl.UsersContextFacade;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UsersContextFacadeImpl implements UsersContextFacade {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    public UsersContextFacadeImpl(UserCommandService userCommandService, UserQueryService userQueryService) {
        this.userCommandService = userCommandService;
        this.userQueryService = userQueryService;
    }

    @Override
    public Long createUser(String name, String email, String password, String role, double income) {
        var createUserCommand = new CreateUserCommand(
                name,
                email,
                password,
                Role.valueOf(role.toUpperCase()), // REPRESENTANTE o MIEMBRO
                BigDecimal.valueOf(income) // <-- convierte el double a BigDecimal
        );
        var user = userCommandService.handle(createUserCommand);
        return user.map(u -> u.getId()).orElse(0L);
    }

    @Override
    public boolean existsUserById(Long id) {
        var query = new GetUserByIdQuery(id);
        return userQueryService.handle(query).isPresent();
    }
}
