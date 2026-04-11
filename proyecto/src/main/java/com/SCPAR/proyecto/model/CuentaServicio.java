package com.SCPAR.proyecto.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cuentas_servicio")
public class CuentaServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "folio_tarjeta")
    private Integer folioTarjeta;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_calle")
    private CatCalle calle;

    // ==========================================
    // CORRECCIONES APLICADAS: Cambiado a String
    // ==========================================
    @Column(name = "numero_casa")
    private String numeroCasa;

    @Column(name = "codigo_postal")
    private String codigoPostal;
    // ==========================================

    @Column(name = "id_servicio")
    private Integer idServicio;

    @Column(name = "descuento_inapam")
    private Boolean descuentoInapam;

    @Column(name = "estatus_cuenta")
    private Integer estatusCuenta;


    // --- GETTERS Y SETTERS ---

    public Integer getFolioTarjeta() {
        return folioTarjeta;
    }

    public void setFolioTarjeta(Integer folioTarjeta) {
        this.folioTarjeta = folioTarjeta;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public CatCalle getCalle() {
        return calle;
    }

    public void setCalle(CatCalle calle) {
        this.calle = calle;
    }

    // Actualizado a String
    public String getNumeroCasa() {
        return numeroCasa;
    }

    // Actualizado a String
    public void setNumeroCasa(String numeroCasa) {
        this.numeroCasa = numeroCasa;
    }

    // Actualizado a String
    public String getCodigoPostal() {
        return codigoPostal;
    }

    // Actualizado a String
    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public Integer getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(Integer idServicio) {
        this.idServicio = idServicio;
    }

    public Boolean getDescuentoInapam() {
        return descuentoInapam;
    }

    public void setDescuentoInapam(Boolean descuentoInapam) {
        this.descuentoInapam = descuentoInapam;
    }

    public Integer getEstatusCuenta() {
        return estatusCuenta;
    }

    public void setEstatusCuenta(Integer estatusCuenta) {
        this.estatusCuenta = estatusCuenta;
    }
}