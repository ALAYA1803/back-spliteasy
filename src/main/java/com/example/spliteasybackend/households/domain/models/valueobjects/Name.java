package com.example.spliteasybackend.households.domain.models.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor // requerido por JPA
public class Name {

    @Column(name = "name", nullable = false)
    private String value;

    public Name(String value) {
        this.value = value;
    }
}
