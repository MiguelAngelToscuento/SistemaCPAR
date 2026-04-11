package com.SCPAR.proyecto.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cat_tipo_servicios")
public class CatTipoServicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio")
    private int idServicio;
    @Column(name = "nombre_servicio")
    private String nombreServicio;
    private BigDecimal tarifa;

    public CatTipoServicio() {}

    public int getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(int idServicio) {
        this.idServicio = idServicio;
    }

    public String getNombreServicio() {
        return nombreServicio;
    }

    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }

    public BigDecimal getTarifa() {
        return tarifa;
    }

    public void setTarifa(BigDecimal tarifa) {
        this.tarifa = tarifa;
    }
}
