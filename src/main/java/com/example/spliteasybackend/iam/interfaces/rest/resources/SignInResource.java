package com.example.spliteasybackend.iam.interfaces.rest.resources;

public record SignInResource(String username, String password, String captchaToken) {
}
