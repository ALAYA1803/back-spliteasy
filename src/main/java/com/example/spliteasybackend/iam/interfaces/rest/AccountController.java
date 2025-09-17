package com.example.spliteasybackend.iam.interfaces.rest;

import com.example.spliteasybackend.iam.application.internal.commandservices.AccountCommandService;
import com.example.spliteasybackend.iam.interfaces.rest.resources.AccountProfileResource;
import com.example.spliteasybackend.iam.interfaces.rest.resources.ChangePasswordResource;
import com.example.spliteasybackend.iam.interfaces.rest.resources.UpdateProfileResource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    private final AccountCommandService accountService;

    public AccountController(AccountCommandService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/me")
    public ResponseEntity<AccountProfileResource> me(Principal principal) {
        var resource = accountService.getMyProfile(principal.getName());
        return ResponseEntity.ok(resource);
    }

    @PutMapping("/profile")
    public ResponseEntity<AccountProfileResource> updateProfile(Principal principal,
                                                                @Valid @RequestBody UpdateProfileResource body) {
        var resource = accountService.updateMyProfile(principal.getName(), body.username(), body.email());
        return ResponseEntity.ok(resource);
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(Principal principal,
                                               @Valid @RequestBody ChangePasswordResource body) {
        accountService.changeMyPassword(principal.getName(), body.currentPassword(), body.newPassword());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteAccount(Principal principal) {
        accountService.deleteMyAccount(principal.getName());
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleConflict(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
