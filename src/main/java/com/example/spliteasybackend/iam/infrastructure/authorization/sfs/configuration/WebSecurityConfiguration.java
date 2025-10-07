package com.example.spliteasybackend.iam.infrastructure.authorization.sfs.configuration;

import com.example.spliteasybackend.iam.infrastructure.authorization.sfs.pipeline.BearerAuthorizationRequestFilter;
import com.example.spliteasybackend.iam.infrastructure.hashing.bcrypt.BCryptHashingService;
import com.example.spliteasybackend.iam.infrastructure.tokens.jwt.BearerTokenService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
    @Order(100)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(e -> e.authenticationEntryPoint(unauthorizedRequestHandler))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error").permitAll()

                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/authentication/sign-in",
                                "/api/v1/authentication/sign-up",
                                "/api/v1/authentication/forgot-password",
                                "/api/v1/authentication/reset-password"
                        ).permitAll()

                        .requestMatchers("/v3/api-docs/**", "/swagger-ui.html",
                                "/swagger-ui/**", "/swagger-resources/**", "/webjars/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()

                        .requestMatchers("/api/v1/account/**").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/v1/households/**").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/v1/bills/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/bills/**").hasAuthority("ROLE_REPRESENTANTE")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/bills/**").hasAuthority("ROLE_REPRESENTANTE")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/bills/**").hasAuthority("ROLE_REPRESENTANTE")

                        .requestMatchers(HttpMethod.GET, "/api/v1/contributions/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/contributions/**").hasAuthority("ROLE_REPRESENTANTE")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/contributions/**").hasAuthority("ROLE_REPRESENTANTE")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/contributions/**").hasAuthority("ROLE_REPRESENTANTE")

                        .requestMatchers(HttpMethod.GET, "/api/v1/household-members/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/household-members/**").hasAuthority("ROLE_REPRESENTANTE")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/household-members/**").hasAuthority("ROLE_REPRESENTANTE")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/household-members/**").hasAuthority("ROLE_REPRESENTANTE")

                        .requestMatchers(HttpMethod.POST, "/api/v1/member-contributions/*/receipts").authenticated()
                        .requestMatchers(HttpMethod.GET,  "/api/v1/member-contributions/*/receipts").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/receipts/*/approve").hasAuthority("ROLE_REPRESENTANTE")
                        .requestMatchers(HttpMethod.POST, "/api/v1/receipts/*/reject").hasAuthority("ROLE_REPRESENTANTE")

                        .requestMatchers(HttpMethod.GET, "/api/v1/member-contributions/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/member-contributions/**").hasAuthority("ROLE_REPRESENTANTE")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/member-contributions/**").hasAuthority("ROLE_REPRESENTANTE")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/member-contributions/**").hasAuthority("ROLE_REPRESENTANTE")

                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authorizationRequestFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain swaggerSecurityChain(
            HttpSecurity http,
            @Value("${docs.basic.enabled:false}") boolean docsAuthEnabled,
            @Value("${docs.basic.user:docs}") String docsUser,
            @Value("${docs.basic.pass:changeit}") String docsPass
    ) throws Exception {

        http.securityMatcher("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html");

        if (!docsAuthEnabled) {
            http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
            return http.build();
        }

        var encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        var inMemory = new InMemoryUserDetailsManager(
                User.withUsername(docsUser)
                        .password(encoder.encode(docsPass))
                        .roles("DOCS")
                        .build()
        );

        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(inMemory);
        provider.setPasswordEncoder(encoder);

        http
                .csrf(csrf -> csrf.disable())
                .authenticationProvider(provider)
                .authorizeHttpRequests(auth -> auth.anyRequest().hasRole("DOCS"))
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
