package com.ryot.helpdesk.config;

import com.ryot.helpdesk.security.JwtAuthenticationFilter;
import com.ryot.helpdesk.utils.SisVars;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        //Usuarios Control
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/usuarios/listar").hasAnyRole(SisVars.ADMIN, SisVars.SOPORTE)
                        .requestMatchers("/api/usuarios/buscar/**").hasAnyRole(SisVars.ADMIN, SisVars.SOPORTE)
                        .requestMatchers("/api/usuarios/crear").hasRole(SisVars.ADMIN)
                        .requestMatchers("/api/usuarios/actualizar").hasRole(SisVars.ADMIN)
                        .requestMatchers("/api/usuarios/inactivar").hasRole(SisVars.ADMIN)
                        .requestMatchers("/api/usuarios/listar-todos").hasRole(SisVars.ADMIN)

                        //Catlogo administrativos

                        .requestMatchers("/api/categorias-ticket/**").hasRole(SisVars.ADMIN)
                        .requestMatchers("/api/departamentos/**").hasRole(SisVars.ADMIN)
                        //Ticket
                        .requestMatchers("/api/tickets/**").hasAnyRole(SisVars.ADMIN, SisVars.SOPORTE,SisVars.USUARIO)
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }
}