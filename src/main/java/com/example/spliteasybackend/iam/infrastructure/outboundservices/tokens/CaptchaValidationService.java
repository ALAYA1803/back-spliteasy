package com.example.spliteasybackend.iam.infrastructure.outboundservices.tokens;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class CaptchaValidationService {

    private static final Logger log = LoggerFactory.getLogger(CaptchaValidationService.class);
    private final RestTemplate rest = new RestTemplate();

    @Value("${recaptcha.web.secret:}")
    private String webSecret;

    @Value("${recaptcha.android.secret:}")
    private String androidSecret;

    private boolean verify(String token, String secret) {
        if (secret == null || secret.isBlank()) return false;

        String url = "https://www.google.com/recaptcha/api/siteverify";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String,String> body = new LinkedMultiValueMap<>();
        body.add("secret", secret);
        body.add("response", token);

        HttpEntity<MultiValueMap<String,String>> req = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<String> resp = rest.postForEntity(url, req, String.class);
            String payload = resp.getBody() != null ? resp.getBody() : "";
            return payload.contains("\"success\": true");
        } catch (Exception e) {
            log.error("Error verifying reCAPTCHA", e);
            return false;
        }
    }

    public boolean validateForWebOrAndroid(String captchaToken) {
        if (captchaToken == null || captchaToken.isBlank()) return false;
        if (verify(captchaToken, webSecret)) return true;
        return verify(captchaToken, androidSecret);
    }
}
