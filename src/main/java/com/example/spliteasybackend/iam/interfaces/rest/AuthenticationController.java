package com.example.spliteasybackend.iam.interfaces.rest;

import com.example.spliteasybackend.iam.domain.services.UserCommandService;
import com.example.spliteasybackend.iam.interfaces.rest.resources.AuthenticatedUserResource;
import com.example.spliteasybackend.iam.interfaces.rest.resources.SignInResource;
import com.example.spliteasybackend.iam.interfaces.rest.resources.SignUpResource;
import com.example.spliteasybackend.iam.interfaces.rest.resources.UserResource;
import com.example.spliteasybackend.iam.interfaces.rest.transform.AuthenticatedUserResourceFromEntityAssembler;
import com.example.spliteasybackend.iam.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import com.example.spliteasybackend.iam.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
import com.example.spliteasybackend.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/api/v1/authentication", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "Available Authentication Endpoints")
@Validated
public class AuthenticationController {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);
    private final UserCommandService userCommandService;

    public AuthenticationController(UserCommandService userCommandService) {
        this.userCommandService = userCommandService;
    }

    @PostMapping(value = "/sign-in", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Sign-in", description = "Sign-in with the provided credentials.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User authenticated successfully.",
                    content = @Content(schema = @Schema(implementation = AuthenticatedUserResource.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials.")
    })
    public ResponseEntity<AuthenticatedUserResource> signIn(@Valid @RequestBody SignInResource signInResource) {
        var cmd = SignInCommandFromResourceAssembler.toCommandFromResource(signInResource);

        var result = userCommandService.handle(cmd);
        if (result.isEmpty()) {
            log.info("Sign-in failed (invalid credentials)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var pair = result.get(); // left: User, right: token (o lo que uses)
        var resource = AuthenticatedUserResourceFromEntityAssembler
                .toResourceFromEntity(pair.getLeft(), pair.getRight());

        log.debug("Sign-in OK userId={}", pair.getLeft().getId());
        return ResponseEntity.ok(resource);
    }

    @PostMapping(value = "/sign-up", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Sign-up", description = "Sign-up with the provided credentials.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully.",
                    content = @Content(schema = @Schema(implementation = UserResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad request.")
    })
    public ResponseEntity<UserResource> signUp(@Valid @RequestBody SignUpResource signUpResource) {
        var cmd = SignUpCommandFromResourceAssembler.toCommandFromResource(signUpResource);

        var created = userCommandService.handle(cmd);
        if (created.isEmpty()) {
            log.info("Sign-up failed (validation/business rules)");
            return ResponseEntity.badRequest().build();
        }

        var user = created.get();
        var body = UserResourceFromEntityAssembler.toResourceFromEntity(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/users/{id}")
                .buildAndExpand(user.getId())
                .toUri();

        var headers = new HttpHeaders();
        headers.setLocation(location);

        log.debug("Sign-up OK userId={}", user.getId());
        return new ResponseEntity<>(body, headers, HttpStatus.CREATED);
    }
}
