package com.SCPAR.proyecto.repository;

import com.SCPAR.proyecto.model.CatTipoServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatTipoServicioRepository extends JpaRepository<CatTipoServicio, Integer> {
    // Esto nos servirá para buscar si un servicio ya existe al querer cambiarle el precio
    CatTipoServicio findByNombreServicio(String nombreServicio);
}