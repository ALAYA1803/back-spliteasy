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

@Slf4j
@Service
public class PlayIntegrityService {

    @Value("${playintegrity.enabled:true}")
    private boolean enabled;

    @Value("${playintegrity.package-name}")
    private String packageName;

    @Value("${playintegrity.accept-basic-integrity:true}")
    private boolean acceptBasicIntegrity;

    @Value("${playintegrity.allowed-cert-digests:}")
    private String allowedCertDigestsCsv;

    @Value("${playintegrity.service-account-json-base64:}")
    private String serviceAccountJsonBase64;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate rest = new RestTemplate();

    private AccessToken cachedToken;
    private Instant tokenExpiry = Instant.EPOCH;

    public boolean verify(String integrityToken) {
        if (!enabled) return true; // si desactivado, no bloquea
        if (integrityToken == null || integrityToken.isBlank()) return false;

        try {
            String url = String.format(
                    "https://playintegrity.googleapis.com/v1/packages/%s:decodeIntegrityToken",
                    packageName
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(fetchAccessToken());
            headers.setContentType(MediaType.APPLICATION_JSON);

            String body = "{\"integrityToken\":\"" + integrityToken + "\"}";
            ResponseEntity<String> resp = rest.exchange(url, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);

            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                log.error("Play Integrity HTTP {}", resp.getStatusCode());
                return false;
            }

            JsonNode root = objectMapper.readTree(resp.getBody());
            JsonNode tokenPayloadExternal = root.path("tokenPayloadExternal");

            // appIntegrity: package
            String pkg = tokenPayloadExternal.path("appIntegrity").path("packageName").asText(null);
            if (!packageName.equals(pkg)) {
                log.warn("packageName mismatch: {} != {}", pkg, packageName);
                return false;
            }

            // appIntegrity: cert digest (base64)
            Set<String> allowed = parseAllowedDigests();
            Set<String> got = new HashSet<>();
            tokenPayloadExternal.path("appIntegrity").path("certificateSha256Digest").forEach(n -> got.add(n.asText()));
            boolean certOk = got.stream().anyMatch(allowed::contains);
            if (!certOk) {
                log.warn("Cert digest NO permitido. Recibidos={}, Permitidos={}", got, allowed);
                return false;
            }

            // deviceIntegrity
            Set<String> verdicts = new HashSet<>();
            tokenPayloadExternal.path("deviceIntegrity").path("deviceRecognitionVerdict").forEach(n -> verdicts.add(n.asText()));
            boolean deviceOk = verdicts.contains("MEETS_DEVICE_INTEGRITY")
                    || (acceptBasicIntegrity && verdicts.contains("MEETS_BASIC_INTEGRITY"));

            if (!deviceOk) {
                log.warn("Device integrity no cumple. verdicts={}", verdicts);
                return false;
            }

            return true;
        } catch (Exception e) {
            log.error("Error verificando Play Integrity", e);
            return false;
        }
    }

    private Set<String> parseAllowedDigests() {
        if (allowedCertDigestsCsv == null || allowedCertDigestsCsv.isBlank()) return Collections.emptySet();
        String[] parts = allowedCertDigestsCsv.split("\\s*,\\s*");
        return new HashSet<>(Arrays.asList(parts));
    }

    private String fetchAccessToken() throws Exception {
        if (cachedToken != null && tokenExpiry.minusSeconds(60).isAfter(Instant.now())) {
            return cachedToken.getTokenValue();
        }
        if (serviceAccountJsonBase64 == null || serviceAccountJsonBase64.isBlank()) {
            throw new IllegalStateException("PLAY_INTEGRITY_SA_JSON_BASE64 vacío");
        }

        byte[] json = Base64.getDecoder().decode(serviceAccountJsonBase64);
        GoogleCredentials creds = GoogleCredentials
                .fromStream(new ByteArrayInputStream(json))
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/playintegrity"));

        creds.refreshIfExpired();
        cachedToken = creds.getAccessToken();
        tokenExpiry = cachedToken.getExpirationTime().toInstant();
        return cachedToken.getTokenValue();
    }
}
