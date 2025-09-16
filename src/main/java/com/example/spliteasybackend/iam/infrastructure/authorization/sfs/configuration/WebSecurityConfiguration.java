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
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableMethodSecurity
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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors(cors -> cors.configurationSource(req -> {
            var c = new CorsConfiguration();
            c.setAllowedOrigins(List.of("*"));
            c.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            c.setAllowedHeaders(List.of("*"));
            c.setExposedHeaders(List.of("Authorization", "Content-Type"));
            c.setAllowCredentials(false);
            return c;
        }));

        http.csrf(csrf -> csrf.disable())
                .exceptionHandling(e -> e.authenticationEntryPoint(unauthorizedRequestHandler))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/authentication/sign-in",
                                "/api/v1/authentication/sign-up").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui.html",
                                "/swagger-ui/**", "/swagger-resources/**", "/webjars/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/households/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/household-members/**")
                        .hasAnyAuthority("ROLE_MIEMBRO", "ROLE_REPRESENTANTE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/member-contributions/**")
                        .hasAnyAuthority("ROLE_MIEMBRO", "ROLE_REPRESENTANTE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/contributions/**")
                        .hasAnyAuthority("ROLE_MIEMBRO", "ROLE_REPRESENTANTE")
                        .requestMatchers("/api/v1/bills/**").hasAuthority("ROLE_REPRESENTANTE")
                        .requestMatchers("/api/v1/contributions/**").hasAuthority("ROLE_REPRESENTANTE")
                        .requestMatchers("/api/v1/household-members/**").hasAuthority("ROLE_REPRESENTANTE")
                        .requestMatchers("/api/v1/member-contributions/**").hasAuthority("ROLE_REPRESENTANTE")
                        .requestMatchers("/api/v1/settings/**").hasAuthority("ROLE_REPRESENTANTE")
                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authorizationRequestFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
