package com.SCPAR.proyecto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/administrador/nuevo", "/administrador", "/css/**", "/img/**").permitAll() // Rutas públicas (registro, index, imágenes)
                        .anyRequest().authenticated() // Cualquier otra ruta pedirá iniciar sesión
                )
                .formLogin(login -> login
                        .loginPage("/login") // Le decimos cuál es nuestra página de login HTML
                        .defaultSuccessUrl("/dashboard", true) // A dónde va si el login es exitoso
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/") // A dónde va al cerrar sesión
                        .permitAll()
                );

        return http.build();
    }

    // Herramienta para encriptar las contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}