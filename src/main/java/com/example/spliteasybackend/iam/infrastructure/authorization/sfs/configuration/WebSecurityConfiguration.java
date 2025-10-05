package com.example.spliteasybackend.iam.infrastructure.authorization.sfs.configuration;

import com.example.spliteasybackend.iam.infrastructure.authorization.sfs.pipeline.BearerAuthorizationRequestFilter;
import com.example.spliteasybackend.iam.infrastructure.hashing.bcrypt.BCryptHashingService;
import com.example.spliteasybackend.iam.infrastructure.tokens.jwt.BearerTokenService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration {

    private final UserDetailsService userDetailsService;
    private final BearerTokenService tokenService;
    private final BCryptHashingService hashingService;
    private final AuthenticationEntryPoint unauthorizedRequestHandler;

    public WebSecurityConfiguration(
            @Qualifier("defaultUserDetailsService") UserDetailsService userDetailsService,
            BearerTokenService tokenService,
            BCryptHashingService hashingService,
            AuthenticationEntryPoint authenticationEntryPoint) {
        this.userDetailsService = userDetailsService;
        this.tokenService = tokenService;
        this.hashingService = hashingService;
        this.unauthorizedRequestHandler = authenticationEntryPoint;
    }

    @Bean
    public BearerAuthorizationRequestFilter authorizationRequestFilter() {
        return new BearerAuthorizationRequestFilter(tokenService, userDetailsService);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(hashingService);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return hashingService; }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOriginPatterns(List.of(
                "https://new-spliteasy.netlify.app",
                "https://app-spliteasy.netlify.app",
                "http://localhost:4200"
        ));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization","Content-Type","X-Requested-With"));
        cfg.setExposedHeaders(List.of("Authorization","Content-Type"));
        cfg.setAllowCredentials(false); // JWT por header; no cookies
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/v1/**", cfg);
        source.registerCorsConfiguration("/v3/api-docs/**", cfg);
        source.registerCorsConfiguration("/swagger-ui/**", cfg);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(e -> e.authenticationEntryPoint(unauthorizedRequestHandler))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // Preflight y errores
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error").permitAll()

                        // ---- AUTH públicos ----
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/authentication/sign-in",
                                "/api/v1/authentication/sign-up",
                                "/api/v1/authentication/forgot-password",
                                "/api/v1/authentication/reset-password"
                        ).permitAll()

                        // ---- Swagger ----
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui.html",
                                "/swagger-ui/**", "/swagger-resources/**", "/webjars/**").permitAll()

                        //---- Archivos públicos ----
                        .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()

                        // ---- Account (perfil) ----
                        .requestMatchers("/api/v1/account/**").authenticated()

                        // ---- Households ----
                        .requestMatchers(HttpMethod.GET, "/api/v1/households/**").authenticated()

                        // ---- Bills ----
                        .requestMatchers(HttpMethod.GET, "/api/v1/bills/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/bills/**").hasAuthority("ROLE_REPRESENTANTE")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/bills/**").hasAuthority("ROLE_REPRESENTANTE")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/bills/**").hasAuthority("ROLE_REPRESENTANTE")

                        // ---- Contributions ----
                        .requestMatchers(HttpMethod.GET, "/api/v1/contributions/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/contributions/**").hasAuthority("ROLE_REPRESENTANTE")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/contributions/**").hasAuthority("ROLE_REPRESENTANTE")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/contributions/**").hasAuthority("ROLE_REPRESENTANTE")

                        // ---- Household Members ----
                        .requestMatchers(HttpMethod.GET, "/api/v1/household-members/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/household-members/**").hasAuthority("ROLE_REPRESENTANTE")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/household-members/**").hasAuthority("ROLE_REPRESENTANTE")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/household-members/**").hasAuthority("ROLE_REPRESENTANTE")

                        // ---- Payment Receipts ----
                        .requestMatchers(HttpMethod.POST, "/api/v1/member-contributions/*/receipts").authenticated()
                        .requestMatchers(HttpMethod.GET,  "/api/v1/member-contributions/*/receipts").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/receipts/*/approve").hasAuthority("ROLE_REPRESENTANTE")
                        .requestMatchers(HttpMethod.POST, "/api/v1/receipts/*/reject").hasAuthority("ROLE_REPRESENTANTE")

                        // ---- Member Contributions ----
                        .requestMatchers(HttpMethod.GET, "/api/v1/member-contributions/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/member-contributions/**").hasAuthority("ROLE_REPRESENTANTE")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/member-contributions/**").hasAuthority("ROLE_REPRESENTANTE")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/member-contributions/**").hasAuthority("ROLE_REPRESENTANTE")

                        // Fallback
                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authorizationRequestFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
