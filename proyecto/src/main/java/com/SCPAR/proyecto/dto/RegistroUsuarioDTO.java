package com.SCPAR.proyecto.dto;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

public class RegistroUsuarioDTO {

    // EL CAMPO COMO STRING PARA ACEPTAR LETRAS
    private String folioTarjeta;

    // Datos personales
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaRegistro;

    // Datos de la cuenta
    private Integer idCalle;
    private String numeroExterior; // <-- Nuevo
    private String numeroInterior; // <-- Nuevo
    private String codigoPostal;
    private Integer idServicio;
    private Boolean descuentoInapam;

    public RegistroUsuarioDTO() {}

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

    public Integer getIdCalle() { return idCalle; }
    public void setIdCalle(Integer idCalle) { this.idCalle = idCalle; }

    public String getNumeroExterior() { return numeroExterior; }
    public void setNumeroExterior(String numeroExterior) { this.numeroExterior = numeroExterior; }

    public String getNumeroInterior() { return numeroInterior; }
    public void setNumeroInterior(String numeroInterior) { this.numeroInterior = numeroInterior; }

    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }

    public Integer getIdServicio() { return idServicio; }
    public void setIdServicio(Integer idServicio) { this.idServicio = idServicio; }

    public Boolean getDescuentoInapam() { return descuentoInapam; }
    public void setDescuentoInapam(Boolean descuentoInapam) { this.descuentoInapam = descuentoInapam; }
}