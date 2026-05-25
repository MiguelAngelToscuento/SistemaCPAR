package com.SCPAR.proyecto.repository;

import com.SCPAR.proyecto.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {

    Optional<PasswordResetToken> findByToken(String token);

    // CORREGIDO: Usa _Id porque tu variable en Java se llama "id"
    void deleteByAdministrador_Id(Integer id);
}