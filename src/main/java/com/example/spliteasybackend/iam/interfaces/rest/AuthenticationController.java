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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/authentication", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "Available Authentication Endpoints")
@Validated
public class AuthenticationController {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    @Value("${recaptcha.secret-key}")
    private String webSecret;

    @Value("${recaptcha.android-secret-key:}")
    private String androidSecret;

    @Value("${recaptcha.min-score:0.5}")
    private double minScore;

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

    private RestTemplate buildRecaptchaRestTemplate() {
        SimpleClientHttpRequestFactory f = new SimpleClientHttpRequestFactory();
        f.setConnectTimeout((int) Duration.ofSeconds(5).toMillis());
        f.setReadTimeout((int) Duration.ofSeconds(5).toMillis());
        return new RestTemplate(f);
    }

    @SuppressWarnings("unchecked")
    private RecaptchaResponse callGoogleVerify(String token, String secret) {
        if (secret == null || secret.isBlank() || token == null || token.isBlank()) {
            return RecaptchaResponse.failed("missing-input");
        }

        try {
            RestTemplate restTemplate = buildRecaptchaRestTemplate();
            String url = "https://www.google.com/recaptcha/api/siteverify";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("secret", secret);
            form.add("response", token);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);
            ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            Map<String, Object> body = resp.getBody();
            boolean success = body != null && Boolean.TRUE.equals(body.get("success"));

            Double score = null;
            if (body != null && body.get("score") instanceof Number n) {
                score = n.doubleValue();
            }

            List<String> errors = null;
            if (body != null && body.get("error-codes") instanceof List l) {
                errors = (List<String>) l;
            }

            return new RecaptchaResponse(success, score, errors);
        } catch (RestClientException ex) {
            log.warn("reCAPTCHA request error: {}", ex.getMessage());
            return RecaptchaResponse.failed("recaptcha-request-error");
        } catch (Exception ex) {
            log.error("reCAPTCHA unexpected error", ex);
            return RecaptchaResponse.failed("recaptcha-unexpected-error");
        }
    }

    private boolean validateCaptcha(String captchaToken) {
        if (captchaToken == null || captchaToken.isBlank()) return false;

        RecaptchaResponse web = callGoogleVerify(captchaToken, webSecret);
        if (web.successWithScore(minScore)) {
            log.debug("reCAPTCHA OK (WEB). score={}", web.score());
            return true;
        }

        RecaptchaResponse android = callGoogleVerify(captchaToken, androidSecret);
        if (android.successWithScore(minScore)) {
            log.debug("reCAPTCHA OK (ANDROID). score={}", android.score());
            return true;
        }

        log.info("reCAPTCHA invalid. web={} android={}", web, android);
        return false;
    }


    @PostMapping(value = "/sign-in", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Sign-in", description = "Sign-in with the provided credentials.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User authenticated successfully.",
                    content = @Content(schema = @Schema(implementation = AuthenticatedUserResource.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials.")
    })
    public ResponseEntity<AuthenticatedUserResource> signIn(@Valid @RequestBody SignInResource signInResource) {

        String captchaToken = signInResource.captchaToken();
        if (!validateCaptcha(captchaToken)) {
            log.info("Sign-in denied: invalid captcha");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var cmd = SignInCommandFromResourceAssembler.toCommandFromResource(signInResource);
        var result = userCommandService.handle(cmd);
        if (result.isEmpty()) {
            log.info("Sign-in failed: invalid credentials for username={}", signInResource.username());
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
        if (!validateCaptcha(signUpResource.captchaToken())) {
            log.info("Sign-up denied: invalid captcha");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        var cmd = SignUpCommandFromResourceAssembler.toCommandFromResource(signUpResource);
        var created = userCommandService.handle(cmd);
        if (created.isEmpty()) {
            log.info("Sign-up failed (business validation)");
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
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Request accepted.") })
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
        // Aquí normalmente dispararías un correo. Por ahora devolvemos el token (útil para pruebas).
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

    private record RecaptchaResponse(boolean success, Double score, List<String> errorCodes) {

        static RecaptchaResponse failed(String code) {
            return new RecaptchaResponse(false, null, List.of(code));
        }

        boolean successWithScore(double minScore) {
            if (!success) return false;
            if (score == null) return true;
            return score >= minScore;
        }

        @Override
        public String toString() {
            return "success=" + success + ", score=" + score + ", errors=" + errorCodes;
        }
    }
}
