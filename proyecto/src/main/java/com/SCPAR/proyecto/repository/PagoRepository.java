package com.SCPAR.proyecto.repository;

import com.SCPAR.proyecto.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {

    @Query("SELECT MAX(p.fechaPago) FROM Pago p WHERE p.cuenta.folioTarjeta = :folio")
    LocalDateTime findUltimaFechaByFolio(@Param("folio") String folio); // Cambiado a String

    // Busca todos los pagos de un folio y los ordena de más reciente a más viejo
    List<Pago> findByCuenta_FolioTarjetaOrderByFechaPagoDesc(String folioTarjeta); // Cambiado a String
}