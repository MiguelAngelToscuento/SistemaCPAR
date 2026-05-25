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
                        // 1. Pantallas públicas (Cualquiera puede entrar sin iniciar sesión)
                        // AQUÍ SE AGREGARON LAS RUTAS DE RECUPERACIÓN Y EL LOGO
                        .requestMatchers("/", "/administrador/nuevo", "/administrador", "/css/**", "/img/**", "/static", "/LOGO.png", "/olvide-password", "/restablecer-password").permitAll()

                        // 2. Cualquier otra pantalla estará bloqueada y pedirá login
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        // 3. Le decimos dónde está TU diseño de login
                        .loginPage("/administrador/login")
                        // 4. Le decimos a Spring que él atrape los datos cuando des clic en "Entrar"
                        .loginProcessingUrl("/administrador/login")
                        // 5. A dónde te manda si la contraseña es correcta
                        .defaultSuccessUrl("/administrador/menu?login=exito", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/") // A dónde vas al cerrar sesión
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