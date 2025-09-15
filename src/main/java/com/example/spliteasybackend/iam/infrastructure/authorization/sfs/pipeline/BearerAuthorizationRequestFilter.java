package com.example.spliteasybackend.iam.infrastructure.authorization.sfs.pipeline;

import com.example.spliteasybackend.iam.infrastructure.authorization.sfs.model.UsernamePasswordAuthenticationTokenBuilder;
import com.example.spliteasybackend.iam.infrastructure.tokens.jwt.BearerTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class BearerAuthorizationRequestFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(BearerAuthorizationRequestFilter.class);

    private final BearerTokenService tokenService;
    private final UserDetailsService userDetailsService;

    public BearerAuthorizationRequestFilter(
            BearerTokenService tokenService,
            @Qualifier("defaultUserDetailsService") UserDetailsService userDetailsService) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String uri = request.getRequestURI();

        if (uri.startsWith("/api/v1/authentication")
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/swagger-ui")
                || uri.startsWith("/swagger-resources")
                || uri.startsWith("/webjars")
                || uri.equals("/error")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String raw = tokenService.getBearerTokenFrom(request);
            if (raw != null && tokenService.validateToken(raw)) {
                final String username = tokenService.getUsernameFromToken(raw);
                var userDetails = userDetailsService.loadUserByUsername(username);

                var auth = UsernamePasswordAuthenticationTokenBuilder.build(userDetails, request);
                SecurityContextHolder.getContext().setAuthentication(auth);

                if (log.isDebugEnabled()) {
                    log.debug("JWT OK: user={}, authorities={}", username, auth.getAuthorities());
                }
            } else if (log.isDebugEnabled()) {
                log.debug("No/invalid JWT on {}", uri);
            }
        } catch (Exception e) {
            log.error("JWT filter error on {}: {}", uri, e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }
}
