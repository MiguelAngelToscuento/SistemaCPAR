package com.SCPAR.proyecto.service;

import com.SCPAR.proyecto.model.Administrador;
import java.util.List;
import com.SCPAR.proyecto.repository.AdministradorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdministradorService {
    //aqui se ven los métodos
    @Autowired
    private AdministradorRepository administradorRepository;

    public List<Administrador> listarTodas(){
        return administradorRepository.findAll();
    }

    public Administrador guardarAdministrador(Administrador administrador){
        return administradorRepository.save(administrador);
    }

    public Administrador obtenderAdministradorById(Integer id){
        return administradorRepository.findById(id).orElse(null);
    }

    public void eliminarAdministrador(Integer id){
        administradorRepository.deleteById(id);
    }
}
