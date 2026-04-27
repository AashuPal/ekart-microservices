//package com.infy.ekart.config;
//
//import com.infy.ekart.security.JwtFilter;
//import org.springframework.context.annotation.*;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.*;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//@Configuration
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
//
//        http
//            .csrf(csrf -> csrf.disable())
//            .authorizeHttpRequests(auth -> auth
//                // Public auth endpoints
//                .requestMatchers("/auth/**").permitAll()
//                // Swagger UI & OpenAPI docs (public)
//                .requestMatchers(
//                    "/swagger-ui.html",
//                    "/swagger-ui/**",
//                    "/v3/api-docs/**"
//                ).permitAll()
//                // All other requests require authentication
//                .anyRequest().authenticated()
//            )
//            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
//            .formLogin(form -> form.disable())
//            .httpBasic(basic -> basic.disable());
//
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder encoder() {
//        return new BCryptPasswordEncoder(12);
//    }
//}


package com.infy.ekart.config;

import com.infy.ekart.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // CORS Configuration
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Disable CSRF for REST APIs
            .csrf(csrf -> csrf.disable())
            
            // Stateless session (no HTTP session)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // URL Authorization
            .authorizeHttpRequests(auth -> auth
                // Public auth endpoints
                .requestMatchers(
                    "/auth/register",
                    "/auth/login",
                    "/auth/forgot-password",
                    "/auth/reset-password"
                ).permitAll()
                
                // Swagger UI & OpenAPI docs (public)
                .requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()
                
                // Actuator health check (public)
                .requestMatchers("/actuator/health").permitAll()
                
                // H2 Console (for development)
                .requestMatchers("/h2-console/**").permitAll()
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            
            // Add JWT filter before UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            
            // Disable form login and HTTP basic
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            
            // Disable logout (JWT handles this)
            .logout(logout -> logout.disable());

        return http.build();
    }

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        
//        // Allowed Origins
//        configuration.setAllowedOrigins(Arrays.asList(
//            "http://localhost:3000",      // React (CRA)
//            "http://localhost:5173",      // React (Vite)
//            "http://localhost:4200",      // Angular
//            "http://localhost:8080",      // API Gateway
//            "http://localhost:8081",      // Auth Service Swagger
//            "http://localhost:8082"       // Cart Service Swagger
//        ));
//        
//        // Allowed Methods
//        configuration.setAllowedMethods(Arrays.asList(
//            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
//        ));
//        
//        // Allowed Headers
//        configuration.setAllowedHeaders(Arrays.asList(
//            "Authorization",
//            "Content-Type",
//            "X-Requested-With",
//            "Accept",
//            "Origin",
//            "Access-Control-Request-Method",
//            "Access-Control-Request-Headers",
//            "X-User-Id",
//            "X-Correlation-Id"
//        ));
//        
//        // Exposed Headers
//        configuration.setExposedHeaders(Arrays.asList(
//            "Authorization",
//            "X-Total-Count",
//            "X-Correlation-Id"
//        ));
//        
//        // Allow credentials (cookies, authorization headers)
//        configuration.setAllowCredentials(true);
//        
//        // Max age for preflight requests (1 hour)
//        configuration.setMaxAge(3600L);
//        
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        
//        return source;
//    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}