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
import com.example.spliteasybackend.iam.infrastructure.hashing.bcrypt.BCryptHashingService;
import com.example.spliteasybackend.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.example.spliteasybackend.iam.infrastructure.tokens.jwt.BearerTokenService;
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
    private final BearerTokenService tokenService;
    private final UserRepository userRepository;
    private final BCryptHashingService hashingService;

    public AuthenticationController(UserCommandService userCommandService,
                                    BearerTokenService tokenService,
                                    UserRepository userRepository,
                                    BCryptHashingService hashingService) {
        this.userCommandService = userCommandService;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.hashingService = hashingService;
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

        var pair = result.get();
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

    public record ForgotPasswordRequest(String email) {}
    public record ForgotPasswordResponse(String message, String resetToken) {}
    public record BasicMessage(String message) {}
    public record ResetPasswordRequest(String token, String newPassword) {}

    @PostMapping(value = "/forgot-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Forgot password", description = "Generates a short-lived reset token and sends instructions.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request accepted.")
    })
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(@RequestBody ForgotPasswordRequest req) {
        if (req == null || req.email() == null || req.email().isBlank()) {
            return ResponseEntity.ok(new ForgotPasswordResponse("Si el email existe, enviaremos instrucciones.", null));
        }

        var userOpt = userRepository.findByEmail(req.email());
        if (userOpt.isEmpty()) {
            return ResponseEntity.ok(new ForgotPasswordResponse("Si el email existe, enviaremos instrucciones.", null));
        }

        var user = userOpt.get();
        String token = tokenService.generateResetToken(user.getId(), 15);

        return ResponseEntity.ok(new ForgotPasswordResponse("Hemos enviado instrucciones a tu correo.", token));
    }

    @PostMapping(value = "/reset-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Reset password", description = "Resets user password using a valid reset token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password updated."),
            @ApiResponse(responseCode = "400", description = "Invalid token or request.")
    })
    public ResponseEntity<BasicMessage> resetPassword(@RequestBody ResetPasswordRequest req) {
        if (req == null || req.token() == null || req.token().isBlank()
                || req.newPassword() == null || req.newPassword().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BasicMessage("Datos incompletos."));
        }

        try {
            Long userId = tokenService.validateAndExtractUserIdFromResetToken(req.token());
            var user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

            user.setPassword(hashingService.encode(req.newPassword()));
            userRepository.save(user);

            return ResponseEntity.ok(new BasicMessage("Contraseña actualizada."));
        } catch (IllegalArgumentException ex) {
            log.warn("reset-password invalid: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BasicMessage(ex.getMessage()));
        } catch (Exception ex) {
            log.error("reset-password error", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BasicMessage("Token inválido o expirado."));
        }
    }
}
