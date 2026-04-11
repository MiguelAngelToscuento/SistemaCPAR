package com.SCPAR.proyecto.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cat_calles")
public class CatCalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_calle")
    private Integer idCalle;

    @Column(name = "nombre_calle", nullable = false, length = 100)
    private String nombreCalle;

    public CatCalle() {}

    // Getters y Setters
    public Integer getIdCalle() {
        return idCalle;
    }

    public void setIdCalle(Integer idCalle) {
        this.idCalle = idCalle;
    }

    public String getNombreCalle() {
        return nombreCalle;
    }

    public void setNombreCalle(String nombreCalle) {
        this.nombreCalle = nombreCalle;
    }
}