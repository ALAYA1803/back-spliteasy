package com.example.spliteasybackend.iam.application.internal.commandservices;

import com.example.spliteasybackend.iam.domain.model.aggregates.User;
import com.example.spliteasybackend.iam.infrastructure.hashing.bcrypt.BCryptHashingService;
import com.example.spliteasybackend.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.example.spliteasybackend.iam.interfaces.rest.resources.AccountProfileResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountCommandServiceImpl implements AccountCommandService {

    private final UserRepository userRepository;
    private final BCryptHashingService hashingService;

    public AccountCommandServiceImpl(UserRepository userRepository,
                                     BCryptHashingService hashingService) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
    }

    private User getUserOrThrow(String principalName) {
        return userRepository.findByUsername(principalName)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado para la sesión actual."));
    }

    @Override
    @Transactional(readOnly = true)
    public AccountProfileResource getMyProfile(String principalName) {
        var u = getUserOrThrow(principalName);
        return new AccountProfileResource(u.getId(), u.getUsername(), u.getEmail());
    }

    @Override
    @Transactional
    public AccountProfileResource updateMyProfile(String principalName, String username, String email) {
        var u = getUserOrThrow(principalName);
        if (!u.getUsername().equalsIgnoreCase(username)) {
            if (userRepository.existsByUsername(username)) {
                throw new IllegalStateException("El nombre de usuario ya está en uso.");
            }
            u.setUsername(username);
        }
        u.setEmail(email);

        try {
            userRepository.save(u);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException("El correo ya está en uso por otro usuario.");
        }

        return new AccountProfileResource(u.getId(), u.getUsername(), u.getEmail());
    }

    @Override
    @Transactional
    public void changeMyPassword(String principalName, String currentPassword, String newPassword) {
        var u = getUserOrThrow(principalName);
        if (!hashingService.matches(currentPassword, u.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual no es correcta.");
        }
        u.setPassword(hashingService.encode(newPassword));
        userRepository.save(u);
    }

    @Override
    @Transactional
    public void deleteMyAccount(String principalName) {
        var u = getUserOrThrow(principalName);
        try {
            userRepository.deleteById(u.getId());
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException("No se pudo eliminar la cuenta porque tiene información vinculada.");
        }
    }
}
