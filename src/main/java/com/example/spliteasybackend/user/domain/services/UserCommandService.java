package com.example.spliteasybackend.user.domain.services;

import com.example.spliteasybackend.user.domain.models.aggregates.User;
import com.example.spliteasybackend.user.domain.models.commands.CreateUserCommand;

import java.util.Optional;

public interface UserCommandService {

    Optional<User> handle(CreateUserCommand command);

    Optional<User> update(Long id, CreateUserCommand command);

    boolean delete(Long id);
}
