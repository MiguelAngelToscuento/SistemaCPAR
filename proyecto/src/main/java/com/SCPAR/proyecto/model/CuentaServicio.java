package com.SCPAR.proyecto.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "cuentas_servicio")
public class CuentaServicio {

    @Id
    @Column(name = "folio_tarjeta")
    private String folioTarjeta;

    @Column(nullable = false)
    private String nombres;

    @Column(name = "apellido_paterno", nullable = false)
    private String apellidoPaterno;

    @Column(name = "apellido_materno")
    private String apellidoMaterno;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro;

    @ManyToOne
    @JoinColumn(name = "id_calle")
    private CatCalle calle;

    @Column(name = "codigo_postal")
    private String codigoPostal;

    @Column(name = "id_servicio")
    private Integer idServicio;

    @Column(name = "descuento_inapam")
    private Boolean descuentoInapam;

    @Column(name = "estatus_cuenta")
    private Integer estatusCuenta;

    @Column(name = "numero_exterior", nullable = false)
    private String numeroExterior;

    @Column(name = "numero_interior")
    private String numeroInterior;

    public CuentaServicio() {}

    // --- GETTERS Y SETTERS ---
    public String getFolioTarjeta() { return folioTarjeta; }
    public void setFolioTarjeta(String folioTarjeta) { this.folioTarjeta = folioTarjeta; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidoPaterno() { return apellidoPaterno; }
    public void setApellidoPaterno(String apellidoPaterno) { this.apellidoPaterno = apellidoPaterno; }

    public String getApellidoMaterno() { return apellidoMaterno; }
    public void setApellidoMaterno(String apellidoMaterno) { this.apellidoMaterno = apellidoMaterno; }

    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public CatCalle getCalle() { return calle; }
    public void setCalle(CatCalle calle) { this.calle = calle; }

    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }

    public Integer getIdServicio() { return idServicio; }
    public void setIdServicio(Integer idServicio) { this.idServicio = idServicio; }

    public Boolean getDescuentoInapam() { return descuentoInapam; }
    public void setDescuentoInapam(Boolean descuentoInapam) { this.descuentoInapam = descuentoInapam; }

    public Integer getEstatusCuenta() { return estatusCuenta; }
    public void setEstatusCuenta(Integer estatusCuenta) { this.estatusCuenta = estatusCuenta; }

    public String getNumeroExterior() { return numeroExterior; }
    public void setNumeroExterior(String numeroExterior) { this.numeroExterior = numeroExterior; }

    public String getNumeroInterior() { return numeroInterior; }
    public void setNumeroInterior(String numeroInterior) { this.numeroInterior = numeroInterior; }
}