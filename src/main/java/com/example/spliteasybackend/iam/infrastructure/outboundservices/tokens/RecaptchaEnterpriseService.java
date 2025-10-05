package com.example.spliteasybackend.iam.infrastructure.outboundservices.tokens;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import java.util.Base64;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.*;

/**
 * NOTA: Mantenemos el mismo nombre del servicio para NO tocar tu controller.
 * Internamente, verifyAndroid() ahora usa la API de Play Integrity "decodeIntegrityToken".
 */
@Slf4j
@Service
public class RecaptchaEnterpriseService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final RestTemplate rest = new RestTemplate();

    // ======= Propiedades Play Integrity (ya las tienes en application.properties) =======
    @Value("${playintegrity.enabled:true}")
    private boolean integrityEnabled;

    @Value("${playintegrity.package-name}")
    private String expectedPackage;

    @Value("${playintegrity.allowed-cert-digests:}")
    private String allowedCertDigestsCsv;

    @Value("${playintegrity.accept-basic-integrity:true}")
    private boolean acceptBasicIntegrity;

    @Value("${playintegrity.service-account-json-base64:}")
    private String saJsonBase64;

    // Token cache
    private AccessToken cachedToken;
    private Instant tokenExpiry = Instant.EPOCH;

    // ======= RESULTADO COMPATIBLE CON TU CONTROLLER =======
    public record AndroidResult(boolean ok, double score, String reason) {}

    /**
     * TU CONTROLLER LLAMA ASÍ:
     *   var andr = recaptchaEnterpriseService.verifyAndroid(captchaToken, "LOGIN", ANDROID_EXPECTED_PACKAGE, minScore)
     *   if (andr.ok()) ...
     *
     * Aquí ignoramos minScore (no aplica a Play Integrity) y usamos device verdicts.
     * El "score" lo devolvemos 1.0 si pasa (para mantener compatibilidad de tipos).
     */
    public AndroidResult verifyAndroid(String integrityToken, String expectedAction, String expectedPkgFromController, double minScore) {
        if (!integrityEnabled) {
            return new AndroidResult(true, 1.0, "disabled");
        }
        if (integrityToken == null || integrityToken.isBlank()) {
            return new AndroidResult(false, 0.0, "missing-token");
        }

        try {
            String pkg = (expectedPkgFromController != null && !expectedPkgFromController.isBlank())
                    ? expectedPkgFromController
                    : expectedPackage;

            // Llamada a Play Integrity
            String url = "https://playintegrity.googleapis.com/v1/packages/" + pkg + ":decodeIntegrityToken";
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(fetchAccessToken());
            headers.setContentType(MediaType.APPLICATION_JSON);

            String body = "{\"integrityToken\":\"" + integrityToken + "\"}";
            ResponseEntity<String> resp = rest.exchange(url, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                return new AndroidResult(false, 0.0, "http_" + resp.getStatusCode());
            }

            JsonNode root = mapper.readTree(resp.getBody());
            JsonNode payload = root.path("tokenPayloadExternal");
            JsonNode appIntegrity = payload.path("appIntegrity");
            JsonNode deviceIntegrity = payload.path("deviceIntegrity");

            // 1) packageName
            String packageName = appIntegrity.path("packageName").asText(null);
            if (!pkg.equals(packageName)) {
                return new AndroidResult(false, 0.0, "package_mismatch:" + packageName);
            }

            // 2) cert digest (base64)
            Set<String> allowedDigests = parseAllowed(allowedCertDigestsCsv);
            Set<String> gotDigests = new HashSet<>();
            appIntegrity.path("certificateSha256Digest").forEach(n -> gotDigests.add(n.asText()));
            boolean certOk = gotDigests.stream().anyMatch(allowedDigests::contains);
            if (!certOk) {
                return new AndroidResult(false, 0.0, "cert_digest_not_allowed:" + gotDigests);
            }

            // 3) device verdicts
            Set<String> verdicts = new HashSet<>();
            deviceIntegrity.path("deviceRecognitionVerdict").forEach(n -> verdicts.add(n.asText()));
            boolean deviceOk = verdicts.contains("MEETS_DEVICE_INTEGRITY")
                    || (acceptBasicIntegrity && verdicts.contains("MEETS_BASIC_INTEGRITY"));
            if (!deviceOk) {
                return new AndroidResult(false, 0.0, "device_verdict:" + verdicts);
            }

            // Si todo OK → score=1.0 por compatibilidad
            return new AndroidResult(true, 1.0, null);

        } catch (Exception e) {
            log.error("Play Integrity error", e);
            return new AndroidResult(false, 0.0, "exception:" + e.getClass().getSimpleName());
        }
    }

    // ====== Helpers ======
    private Set<String> parseAllowed(String csv) {
        if (csv == null || csv.isBlank()) return Collections.emptySet();
        String[] parts = csv.split("\\s*,\\s*");
        return new HashSet<>(Arrays.asList(parts));
    }

    private String fetchAccessToken() throws Exception {
        if (cachedToken != null && tokenExpiry.minusSeconds(60).isAfter(Instant.now())) {
            return cachedToken.getTokenValue();
        }
        if (saJsonBase64 == null || saJsonBase64.isBlank()) {
            throw new IllegalStateException("PLAY_INTEGRITY_SA_JSON_BASE64 vacío");
        }
        byte[] json = Base64.getDecoder().decode(saJsonBase64);
        GoogleCredentials creds = GoogleCredentials
                .fromStream(new ByteArrayInputStream(json))
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/playintegrity"));
        creds.refreshIfExpired();
        cachedToken = creds.getAccessToken();
        tokenExpiry = cachedToken.getExpirationTime().toInstant();
        return cachedToken.getTokenValue();
    }
}
