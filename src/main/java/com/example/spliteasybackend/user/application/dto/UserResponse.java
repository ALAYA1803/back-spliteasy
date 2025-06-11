package com.example.spliteasybackend.user.application.dto;

import com.example.spliteasybackend.user.domain.models.aggregates.User;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private User.Role role;
    private BigDecimal income;

    public UserResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.income = user.getIncome();
    }
}