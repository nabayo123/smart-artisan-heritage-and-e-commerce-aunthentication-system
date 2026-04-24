package com.korarwanda.kora.config;

import com.korarwanda.kora.security.AuthTokenFilter;
import com.korarwanda.kora.security.JwtUtils;
import com.korarwanda.kora.security.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtils jwtUtils;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter(jwtUtils, userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer::disable)
            .headers(headers -> headers.frameOptions(org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig::disable))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Static resources & pages - must be public
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/register.html",
                    "/login.html",
                    "/*.html",
                    "/static/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/favicon.ico",
                    "/error"
                ).permitAll()
                // Auth & public API endpoints
                .requestMatchers(
                    "/api/auth/**",
                    "/api/health",
                    "/api/products/public/**",
                    "/api/certificates/verify/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/api-docs/**",
                    "/v3/api-docs/**",
                    "/h2-console/**"
                ).permitAll()
                // Admin & Elevated Artisans (assigned full platform privileges for easy access)
                .requestMatchers("/api/admin/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_ARTISAN")
                // Artisan endpoints
                .requestMatchers("/api/artisan/**").hasAnyAuthority("ROLE_ARTISAN", "ROLE_ADMIN")
                // Customer & Artisan purchase endpoints
                .requestMatchers("/api/orders/**", "/api/payments/**").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ARTISAN", "ROLE_ADMIN")
                // Cooperative endpoints
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/cooperatives", "/api/cooperatives/**").permitAll()
                .requestMatchers("/api/cooperatives", "/api/cooperatives/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_ARTISAN")
                // Everything else requires auth
                .anyRequest().authenticated()
            );

        http.exceptionHandling(exception -> exception
            .accessDeniedHandler((request, response, accessDeniedException) -> {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                String roles = SecurityContextHolder.getContext().getAuthentication() != null ? 
                        SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString() : "Anonymous";
                response.getWriter().write("{\"success\":false,\"message\":\"Access Denied: Current roles " + roles + " - Need ROLE_ADMIN\",\"error\":\"Forbidden\"}");
            })
        );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
