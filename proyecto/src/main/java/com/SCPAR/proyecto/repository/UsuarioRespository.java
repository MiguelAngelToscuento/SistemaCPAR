package com.SCPAR.proyecto.repository;

import com.SCPAR.proyecto.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRespository extends JpaRepository<Usuario, Integer> {
}
