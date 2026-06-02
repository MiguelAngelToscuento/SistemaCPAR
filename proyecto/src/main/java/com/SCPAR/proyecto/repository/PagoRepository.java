package com.SCPAR.proyecto.repository;

import com.SCPAR.proyecto.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {

    @Query("SELECT MAX(p.fechaPago) FROM Pago p WHERE p.cuenta.folioTarjeta = :folio")
    LocalDateTime findUltimaFechaByFolio(@Param("folio") String folio); // Cambiado a String

    // Busca todos los pagos de un folio y los ordena de más reciente a más viejo
    List<Pago> findByCuenta_FolioTarjetaOrderByFechaPagoDesc(String folioTarjeta); // Cambiado a String

    //Query para buscar a los deudores y listarlos
    //Query para buscar a los deudores y listarlos (ACTUALIZADO CON FECHA HISTÓRICA)
    @Query(value = "SELECT " +
            "  c.folio_tarjeta AS folio, " +
            "  CONCAT(c.nombres, ' ', c.apellido_paterno, ' ', COALESCE(c.apellido_materno, '')) AS nombre, " +
            "  COALESCE(MAX(d.periodo_cubierto), c.fecha_ultimo_pago) AS ultimo_mes_pagado, " +
            "  CASE " +
            "    WHEN MAX(d.periodo_cubierto) IS NOT NULL THEN TIMESTAMPDIFF(MONTH, MAX(d.periodo_cubierto), CURDATE()) " +
            "    WHEN c.fecha_ultimo_pago IS NOT NULL THEN TIMESTAMPDIFF(MONTH, c.fecha_ultimo_pago, CURDATE()) " +
            "    ELSE TIMESTAMPDIFF(MONTH, c.fecha_registro, CURDATE()) + 1 " +
            "  END AS meses_atraso " +
            "FROM cuentas_servicio c " +
            "LEFT JOIN pagos p ON c.folio_tarjeta = p.folio_tarjeta " +
            "LEFT JOIN detalle_pago d ON p.id_pago = d.id_pago " +
            "WHERE c.estatus_cuenta = 1 " + // Solo cuentas activas
            "GROUP BY c.folio_tarjeta, c.nombres, c.apellido_paterno, c.apellido_materno, c.fecha_registro, c.fecha_ultimo_pago " +
            "HAVING ultimo_mes_pagado IS NULL OR ultimo_mes_pagado < DATE_FORMAT(NOW(), '%Y-%m-01')",
            nativeQuery = true)
    List<Map<String, Object>> findCuentasConAdeudo();
}