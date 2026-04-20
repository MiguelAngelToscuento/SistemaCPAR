package com.SCPAR.proyecto.model;

import jakarta.persistence.*;


@Entity
@Table(name = "administradores")
public class Administrador {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id_admin")
    private Integer id;
    private String correo;
    @Column(name = "password_hash")
    private String password;
    private boolean activo;

    public Administrador() {}

    public Integer getId(){
        return id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password_hash) {
        this.password = password_hash;
    }


    public boolean getActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    // agregar comentarios
}
