package com.SCPAR.proyecto.dto;

import java.time.LocalDateTime;

public class EstadoCuentaDTO {

    private String folioTarjeta;
    private String nombreTitular;
    private String domicilioCompleto;
    private String codigoPostal;
    private String tipoServicio;
    private String descuentoInapamStr;
    private Double montoTotalDeuda;
    private LocalDateTime ultimaFechaPago;
    private int mesesPendientes;
    private Integer estatusCuenta; // <-- Variable del estatus

    public EstadoCuentaDTO() {
    }

    // --- GETTERS Y SETTERS ---
    public String getFolioTarjeta() {
        return folioTarjeta;
    }

    public void setFolioTarjeta(String folioTarjeta) {
        this.folioTarjeta = folioTarjeta;
    }

    public String getNombreTitular() {
        return nombreTitular;
    }

    public void setNombreTitular(String nombreTitular) {
        this.nombreTitular = nombreTitular;
    }

    public String getDomicilioCompleto() {
        return domicilioCompleto;
    }

    public void setDomicilioCompleto(String domicilioCompleto) {
        this.domicilioCompleto = domicilioCompleto;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public String getDescuentoInapamStr() {
        return descuentoInapamStr;
    }

    public void setDescuentoInapamStr(String descuentoInapamStr) {
        this.descuentoInapamStr = descuentoInapamStr;
    }

    public Double getMontoTotalDeuda() {
        return montoTotalDeuda;
    }

    public void setMontoTotalDeuda(Double montoTotalDeuda) {
        this.montoTotalDeuda = montoTotalDeuda;
    }

    public LocalDateTime getUltimaFechaPago() {
        return ultimaFechaPago;
    }

    public void setUltimaFechaPago(LocalDateTime ultimaFechaPago) {
        this.ultimaFechaPago = ultimaFechaPago;
    }

    public int getMesesPendientes() {
        return mesesPendientes;
    }

    public void setMesesPendientes(int mesesPendientes) {
        this.mesesPendientes = mesesPendientes;
    }

    public Integer getEstatusCuenta() {
        return estatusCuenta;
    }

    public void setEstatusCuenta(Integer estatusCuenta) {
        this.estatusCuenta = estatusCuenta;
    }
}
