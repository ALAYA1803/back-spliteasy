package com.example.spliteasybackend.iam.infrastructure.tokens.jwt.services;

import com.example.spliteasybackend.iam.application.internal.outboundservices.tokens.TokenService;
import com.example.spliteasybackend.iam.infrastructure.tokens.jwt.BearerTokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TokenServiceImpl implements TokenService, BearerTokenService {
    private final Logger LOGGER = LoggerFactory.getLogger(TokenServiceImpl.class);

    private static final String AUTHORIZATION_PARAMETER_NAME = "Authorization";
    private static final String BEARER_TOKEN_PREFIX = "Bearer ";
    private static final int TOKEN_BEGIN_INDEX = 7;
    private static final String ROLES_CLAIM = "roles";
    private static final String USER_ID_CLAIM = "uid";

    @Value("${authorization.jwt.secret}")
    private String secret;

    @Value("${authorization.jwt.expiration.days}")
    private int expirationDays;
    @Override
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities =
                authentication.getAuthorities() != null ? authentication.getAuthorities() : List.of();
        return buildToken(username, null, authorities);
    }
    @Override
    public String generateToken(String username) {
        return buildToken(username, null, List.of());
    }

    public String generateToken(String username, Long userId, Collection<? extends GrantedAuthority> authorities) {
        return buildToken(username, userId, authorities);
    }

    private String buildToken(String username, Long userId, Collection<? extends GrantedAuthority> authorities) {
        Date issuedAt = new Date();
        Date expiration = DateUtils.addDays(issuedAt, expirationDays);
        SecretKey key = getSigningKey();

        Map<String, Object> claims = new HashMap<>();
        if (authorities != null) {
            List<String> roles = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            if (!roles.isEmpty()) claims.put(ROLES_CLAIM, roles);
        }
        if (userId != null) claims.put(USER_ID_CLAIM, userId);

        return Jwts.builder()
                .subject(username)
                .claims(claims)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    @Override
    public String getUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public List<String> getRolesFromToken(String token) {
        Claims claims = extractAllClaims(token);
        Object v = claims.get(ROLES_CLAIM);
        if (v instanceof Collection<?> col) {
            return col.stream().map(String::valueOf).collect(Collectors.toList());
        }
        return List.of();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = extractAllClaims(token);
        Object v = claims.get(USER_ID_CLAIM);
        if (v == null) return null;
        try {
            return Long.valueOf(String.valueOf(v));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        }  catch (SignatureException e) {
            LOGGER.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            LOGGER.error("Invalid JWT: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            LOGGER.error("JWT expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            LOGGER.error("JWT unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.error("JWT claims empty: {}", e.getMessage());
        }
        return false;
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private boolean isTokenPresentIn(String authorizationParameter) { return StringUtils.hasText(authorizationParameter); }
    private boolean isBearerTokenIn(String authorizationParameter) { return authorizationParameter.startsWith(BEARER_TOKEN_PREFIX); }
    private String extractTokenFrom(String authorizationHeaderParameter) { return authorizationHeaderParameter.substring(TOKEN_BEGIN_INDEX); }
    private String getAuthorizationParameterFrom(HttpServletRequest request) { return request.getHeader(AUTHORIZATION_PARAMETER_NAME); }

    @Override
    public String getBearerTokenFrom(HttpServletRequest request) {
        String parameter = getAuthorizationParameterFrom(request);
        if (isTokenPresentIn(parameter) && isBearerTokenIn(parameter)) return extractTokenFrom(parameter);
        return null;
    }
}
