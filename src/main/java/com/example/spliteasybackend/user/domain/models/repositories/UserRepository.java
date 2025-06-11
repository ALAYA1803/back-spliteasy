package com.example.spliteasybackend.user.domain.models.repositories;

import com.example.spliteasybackend.user.domain.models.aggregates.User;

import java.util.List;

public interface UserRepository {
    User save(User user);
    List<User> findAll();
}