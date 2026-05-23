package com.SCPAR.proyecto.repository;

import com.SCPAR.proyecto.model.CuentaServicio;
import org.springframework.data.jpa.repository.JpaRepository;

// Cambiado de Integer a String
public interface CuentaServicioRepository extends JpaRepository<CuentaServicio, String> {

}