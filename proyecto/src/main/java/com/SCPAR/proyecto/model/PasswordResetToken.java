package com.SCPAR.proyecto.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_token")
    private Integer idToken;

    @Column(nullable = false, unique = true)
    private String token;

    // Relación con tu entidad Administrador (asumiendo que se llama Administrador)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_admin", nullable = false)
    private Administrador administrador;

    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;

    public PasswordResetToken() {}

    // Método útil para saber si el token ya caducó
    public boolean estaExpirado() {
        return LocalDateTime.now().isAfter(this.fechaExpiracion);
    }

    // --- GETTERS Y SETTERS ---
    public Integer getIdToken() { return idToken; }
    public void setIdToken(Integer idToken) { this.idToken = idToken; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Administrador getAdministrador() { return administrador; }
    public void setAdministrador(Administrador administrador) { this.administrador = administrador; }

    public LocalDateTime getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(LocalDateTime fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }
}