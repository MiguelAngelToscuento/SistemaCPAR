package com.SCPAR.proyecto.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

    @ManyToOne
    @JoinColumn(name = "id_admin", nullable = false)
    private Administrador administrador;

    @Column(name = "monto_total", nullable = false)
    private BigDecimal montoTotal;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    // NUEVO: Relación para traer los meses que se pagaron en este recibo
    @OneToMany(mappedBy = "pago", fetch = FetchType.EAGER)
    private List<DetallePago> detalles;

    @PrePersist
    protected void onCreate() {
        this.fechaPago = LocalDateTime.now();
    }

    public Pago() {}

    // --- NUEVO: MÉTODO PARA DIBUJAR EL PERIODO EN EL HTML ---
    public String getPeriodoPagado() {
        if (detalles == null || detalles.isEmpty()) return "Sin detalles";

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/yyyy");
        String inicio = detalles.get(0).getPeriodoCubierto().format(fmt);

        if (detalles.size() == 1) {
            return inicio + " (1 mes)";
        }

        String fin = detalles.get(detalles.size() - 1).getPeriodoCubierto().format(fmt);
        return inicio + " a " + fin + " (" + detalles.size() + " meses)";
    }

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
    public List<DetallePago> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePago> detalles) { this.detalles = detalles; }
}