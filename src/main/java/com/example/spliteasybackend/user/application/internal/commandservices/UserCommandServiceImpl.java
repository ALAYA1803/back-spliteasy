package com.example.spliteasybackend.user.application.internal.commandservices;

import com.example.spliteasybackend.user.domain.models.aggregates.User;
import com.example.spliteasybackend.user.domain.models.commands.CreateUserCommand;
import com.example.spliteasybackend.user.domain.services.UserCommandService;
import com.example.spliteasybackend.user.infrastructure.persistance.jpa.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;

    public UserCommandServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> handle(CreateUserCommand command) {

        var user = new User(command);
        userRepository.save(user);
        return Optional.of(user);
    }

    @Override
    public Optional<User> update(Long id, CreateUserCommand command) {
        var optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) return Optional.empty();

        var user = optionalUser.get();

        user.update(command); // Este método lo crearemos ahora

        userRepository.save(user);
        return Optional.of(user);
    }

    @Override
    public boolean delete(Long id) {
        if (!userRepository.existsById(id)) return false;
        userRepository.deleteById(id);
        return true;
    }
}
