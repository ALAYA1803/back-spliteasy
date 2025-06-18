package com.example.spliteasybackend.user.domain.models.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;

@Embeddable
@EqualsAndHashCode
public class EmailAddress {

    @Column(name = "email", nullable = false, length = 100) // 👈 Asegura el nombre exacto
    private String address;

    protected EmailAddress() {
        // requerido por JPA
    }

    public EmailAddress(String address) {
        if (address == null || !address.contains("@")) {
            throw new IllegalArgumentException("Email inválido: " + address);
        }
        this.address = address;
    }

    public String address() {
        return address;
    }

    @Override
    public String toString() {
        return address;
    }
}
