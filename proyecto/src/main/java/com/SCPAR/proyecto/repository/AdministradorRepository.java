package com.SCPAR.proyecto.repository;

import com.SCPAR.proyecto.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministradorRepository extends JpaRepository<Administrador,Integer> {
    Administrador findByCorreo(String correo);
}
