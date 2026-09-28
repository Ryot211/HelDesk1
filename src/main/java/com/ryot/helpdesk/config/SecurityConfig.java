package com.ryot.helpdesk.config;

import com.ryot.helpdesk.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        // Necesario para CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Login público
                        .requestMatchers("/api/auth/login").permitAll()

                        // Swagger / OpenAPI
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()

                        // Usuarios
                        .requestMatchers("/api/usuarios/listar").hasAnyRole("ADMIN", "SOPORTE")
                        .requestMatchers("/api/usuarios/buscar/**").hasAnyRole("ADMIN", "SOPORTE")
                        .requestMatchers("/api/usuarios/crear").hasRole("ADMIN")
                        .requestMatchers("/api/usuarios/actualizar").hasRole("ADMIN")
                        .requestMatchers("/api/usuarios/inactivar").hasRole("ADMIN")
                        .requestMatchers("/api/usuarios/listar-todos").hasRole("ADMIN")
                        .requestMatchers("/api/usuarios/cambiar-password").hasRole("ADMIN")

                        // Catálogos
                        .requestMatchers("/api/categorias-ticket/**").hasRole("ADMIN")
                        .requestMatchers("/api/departamentos/**").hasRole("ADMIN")
                        .requestMatchers("/api/roles/**").hasRole("ADMIN")

                        // Tickets, comentarios, historial y adjuntos
                        .requestMatchers("/api/tickets/**").hasAnyRole(
                                "ADMIN",
                                "SOPORTE",
                                "USUARIO_FINAL"
                        )

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://127.0.0.1:*"
        ));

        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept"
        ));

        configuration.setExposedHeaders(List.of(
                "Content-Disposition"
        ));

        configuration.setAllowCredentials(true);

        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}