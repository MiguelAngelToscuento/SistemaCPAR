package com.SCPAR.proyecto.repository;

import com.SCPAR.proyecto.model.DetallePago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetallePagoRepository extends JpaRepository<DetallePago, Integer> {
}