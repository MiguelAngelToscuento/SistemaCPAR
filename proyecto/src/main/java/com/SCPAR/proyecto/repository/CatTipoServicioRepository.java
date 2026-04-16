package com.SCPAR.proyecto.repository;

import com.SCPAR.proyecto.model.CatTipoServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatTipoServicioRepository extends JpaRepository<CatTipoServicio, Integer> {
}