package com.example.spliteasybackend.user.application.internal.queryservices;

import com.example.spliteasybackend.user.domain.models.aggregates.User;
import com.example.spliteasybackend.user.domain.models.queries.GetAllUsersQuery;
import com.example.spliteasybackend.user.domain.models.queries.GetUserByIdQuery;
import com.example.spliteasybackend.user.domain.services.UserQueryService;
import com.example.spliteasybackend.user.infrastructure.persistance.jpa.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    public UserQueryServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> handle(GetUserByIdQuery query) {
        return userRepository.findById(query.id());
    }

    @Override
    public List<User> handle(GetAllUsersQuery query) {
        return userRepository.findAll();
    }
}
