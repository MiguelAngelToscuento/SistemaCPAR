package com.SCPAR.proyecto.service;

import com.SCPAR.proyecto.model.Administrador;
import com.SCPAR.proyecto.repository.AdministradorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AdministradorRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Administrador admin = adminRepository.findByCorreo(correo);
        if (admin == null) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
        // Convierte tu Administrador en un "Usuario" que Spring Security entiende
        return User.builder()
                .username(admin.getCorreo())
                .password(admin.getPassword()) // Debe estar encriptada
                .roles("ADMIN")
                .build();
    }
}