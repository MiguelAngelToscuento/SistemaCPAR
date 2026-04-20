package com.SCPAR.proyecto.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Integer idPago;

    @ManyToOne
    @JoinColumn(name = "folio_tarjeta", nullable = false)
    private CuentaServicio cuenta;

    // NUEVO: Relación para saber qué Administrador hizo el cobro
    @ManyToOne
    @JoinColumn(name = "id_admin", nullable = false)
    private Administrador administrador;

    @Column(name = "monto_total", nullable = false)
    private BigDecimal montoTotal;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @PrePersist
    protected void onCreate() {
        this.fechaPago = LocalDateTime.now(); // Genera la fecha exacta al guardar
    }

    public Pago() {}

    // --- Getters y Setters ---
    public Integer getIdPago() { return idPago; }
    public void setIdPago(Integer idPago) { this.idPago = idPago; }
    public CuentaServicio getCuenta() { return cuenta; }
    public void setCuenta(CuentaServicio cuenta) { this.cuenta = cuenta; }
    public Administrador getAdministrador() { return administrador; }
    public void setAdministrador(Administrador administrador) { this.administrador = administrador; }
    public BigDecimal getMontoTotal() { return montoTotal; }
    public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }
    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }
}