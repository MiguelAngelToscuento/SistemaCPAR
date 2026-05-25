package com.SCPAR.proyecto.repository;

import com.SCPAR.proyecto.model.CatCalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatCalleRepository extends JpaRepository<CatCalle, Integer> {
    // Esto nos servirá para evitar que registren la misma calle dos veces
    boolean existsByNombreCalle(String nombreCalle);
}