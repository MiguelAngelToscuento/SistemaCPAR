package com.SCPAR.proyecto.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "detalle_pago")
public class DetallePago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    @ManyToOne
    @JoinColumn(name = "id_pago", nullable = false)
    private Pago pago;

    @Column(name = "periodo_cubierto", nullable = false)
    private LocalDate periodoCubierto;

    @Column(name = "monto_aplicado", nullable = false)
    private BigDecimal montoAplicado;

    public DetallePago() {}

    // --- Getters y Setters ---
    public Integer getIdDetalle() { return idDetalle; }
    public void setIdDetalle(Integer idDetalle) { this.idDetalle = idDetalle; }
    public Pago getPago() { return pago; }
    public void setPago(Pago pago) { this.pago = pago; }
    public LocalDate getPeriodoCubierto() { return periodoCubierto; }
    public void setPeriodoCubierto(LocalDate periodoCubierto) { this.periodoCubierto = periodoCubierto; }
    public BigDecimal getMontoAplicado() { return montoAplicado; }
    public void setMontoAplicado(BigDecimal montoAplicado) { this.montoAplicado = montoAplicado; }
}