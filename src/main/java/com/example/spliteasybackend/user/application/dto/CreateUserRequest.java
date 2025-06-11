package com.example.spliteasybackend.user.application.dto;

import com.example.spliteasybackend.user.domain.models.aggregates.User;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateUserRequest {
    private String name;
    private String email;
    private String password;
    private User.Role role;
    private BigDecimal income;
}