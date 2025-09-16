package com.example.spliteasybackend.settings.domain.models.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Language {
    @Column(name = "language")
    private String code;

    protected Language() {}

    public Language(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
