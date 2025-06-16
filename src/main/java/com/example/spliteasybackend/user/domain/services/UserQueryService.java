package com.example.spliteasybackend.user.domain.services;

import com.example.spliteasybackend.user.domain.models.aggregates.User;
import com.example.spliteasybackend.user.domain.models.queries.GetAllUsersQuery;
import com.example.spliteasybackend.user.domain.models.queries.GetUserByIdQuery;

import java.util.List;
import java.util.Optional;

public interface UserQueryService {

    Optional<User> handle(GetUserByIdQuery query);

    List<User> handle(GetAllUsersQuery query);

}
