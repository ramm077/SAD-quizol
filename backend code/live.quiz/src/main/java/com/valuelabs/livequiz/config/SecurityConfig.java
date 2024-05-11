package com.valuelabs.livequiz.config;
import com.valuelabs.livequiz.exception.SecurityExceptionHandlerFilter;
import com.valuelabs.livequiz.security.CustomUserDetailsService;
import com.valuelabs.livequiz.security.JwtAuthFilter;
import com.valuelabs.livequiz.security.UserAuthEntryPoint;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
/**
 * The SecurityConfig class configures Spring Security settings.
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserAuthEntryPoint userAuthEntryPoint;
    private final SecurityExceptionHandlerFilter securityExceptionHandlerFilter;
    @Autowired
    public SecurityConfig(JwtAuthFilter jwtAuthFilter, CustomUserDetailsService customUserDetailsService, UserAuthEntryPoint userAuthEntryPoint, SecurityExceptionHandlerFilter securityExceptionHandlerFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.customUserDetailsService = customUserDetailsService;
        this.userAuthEntryPoint = userAuthEntryPoint;
        this.securityExceptionHandlerFilter = securityExceptionHandlerFilter;
    }
    /**
     * Generates a base64-encoded secret key for JWT.
     * @return The base64-encoded secret key.
     */
    private String base64EncodedSecretKey(){
        log.info("Inside SecurityConfig, base64EncodedSecretKey method!");
        byte[] keyBytes = Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded();
        log.info("Successfully generated the secretKey, returning the secretKey string!");
        return Base64.getEncoder().encodeToString(keyBytes);
    }
    /**
     * Creates a SecretKey bean from the base64-encoded secret key.
     * @return The SecretKey bean.
     */
    @Bean
    public SecretKey secretKey() {
        log.info("Inside SecurityConfig, secretKey method!");
        byte[] decodedKey = Base64.getDecoder().decode(base64EncodedSecretKey());
        log.info("Successfully created the decodedKey, returning it using Signature HmacSHA256 method!");
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
    }
    /**
     * Configures the security filter chain.
     * @param http The HttpSecurity object to configure.
     * @return The SecurityFilterChain bean.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Inside SecurityConfig, securityFilterChain method!");
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(customizer -> customizer.authenticationEntryPoint(userAuthEntryPoint))
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(securityExceptionHandlerFilter, JwtAuthFilter.class)
                .authorizeHttpRequests(request -> request
//                                .anyRequest().permitAll()
                                .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                );
        log.info("Security filter chain configured successfully.");
        return http.build();
    }
    /**
     * Creates an AuthenticationProvider bean.
     * @return The AuthenticationProvider bean.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        log.info("Inside SecurityConfig, authenticationProvider method!");
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(customUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        log.info("AuthenticationProvider bean created.");
        return authenticationProvider;
    }
    /**
     * Creates a PasswordEncoder bean.
     * @return The PasswordEncoder bean.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("PasswordEncoder bean created.");
        return new BCryptPasswordEncoder();
    }
    /**
     * Creates an AuthenticationManager bean.
     * @param config The AuthenticationConfiguration object.
     * @return The AuthenticationManager bean.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        log.info("AuthenticationManager bean created.");
        return config.getAuthenticationManager();
    }
}