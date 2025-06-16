package com.example.spliteasybackend.user.infrastructure.persistance.jpa.repositories;

import com.example.spliteasybackend.user.domain.models.aggregates.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // No necesitas más métodos si solo usas ID
}
