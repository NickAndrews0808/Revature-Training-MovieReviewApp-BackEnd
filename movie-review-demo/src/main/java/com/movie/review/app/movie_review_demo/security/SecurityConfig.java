package com.movie.review.app.movie_review_demo.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 📚 LESSON: SecurityConfig - The Security Blueprint
 * 
 * This is like the building's security policy document:
 * - Which doors are public (login, register)
 * - Which doors need badge access (protected routes)
 * - How to verify badges (authentication)
 * - Which security guards to use (filters)
 * 
 * Spring Security automatically protects ALL endpoints.
 * We configure which ones to make public and which to protect.
 */
@Configuration
public class SecurityConfig {
    
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private BCryptPasswordEncoder bCryptEncoder;
    
    @Autowired
    private SecurityFilter securityFilter;

    /**
     * 📚 LESSON: Authentication Manager
     * 
     * This is the "head of security" that manages all authentication.
     * Used in login endpoint to verify credentials.
     * 
     * @param authConfig - Spring's authentication configuration
     * @return AuthenticationManager bean
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) 
            throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * 📚 LESSON: Security Filter Chain - The Main Security Rules
     * 
     * This defines:
     * 1. CORS policy (which frontend domains can call our API)
     * 2. CSRF protection (disabled for REST APIs)
     * 3. Which endpoints are public vs protected
     * 4. Session management (stateless for JWT)
     * 5. Which filters to use
     * 
     * Think of it as writing the building's access control rules.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1️⃣ CORS Configuration
            // Allow frontend (http://localhost:5173) to call our API
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfig = new CorsConfiguration();
                corsConfig.setAllowedOrigins(List.of("http://localhost:5173"));
                corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                corsConfig.setAllowedHeaders(List.of("*"));
                corsConfig.setAllowCredentials(true);
                return corsConfig;
            }))
            
            // 2️⃣ Disable CSRF
            // CSRF protection not needed for stateless REST APIs with JWT
            // (CSRF is for session-based authentication)
            .csrf(csrf -> csrf.disable())
            
            // 3️⃣ Authorization Rules
            .authorizeHttpRequests(auth -> auth
                // PUBLIC ENDPOINTS (no token needed)
                // ✅ /users/create - Registration
                // ✅ /auth/login - Login
                .requestMatchers("/users/create", "/auth/login").permitAll()
                
                // PROTECTED ENDPOINTS (token required)
                // 🔒 Everything else needs authentication
                .anyRequest().authenticated()
            )
            
            // 4️⃣ Session Management
            // STATELESS = No server-side sessions
            // Perfect for JWT tokens (token carries all info)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        
        // 5️⃣ Add our custom SecurityFilter
        // Runs BEFORE Spring's default authentication filter
        // This checks JWT tokens on every request
        http.addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);
        
        // 6️⃣ Set authentication provider
        // Tells Spring how to verify user credentials
        http.authenticationProvider(authenticationProvider());
        
        return http.build();
    }

    /**
     * 📚 LESSON: Authentication Provider
     * 
     * This tells Spring Security:
     * - How to load user details (from database via UserDetailsService)
     * - How to verify passwords (using BCrypt)
     * 
     * It's like training the security guard:
     * - Where to check ID records (database)
     * - How to verify signatures (password hashing)
     * 
     * @return Configured authentication provider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = 
            new DaoAuthenticationProvider(userDetailsService);
        
        authenticationProvider.setPasswordEncoder(bCryptEncoder);
        
        return authenticationProvider;
    }
}