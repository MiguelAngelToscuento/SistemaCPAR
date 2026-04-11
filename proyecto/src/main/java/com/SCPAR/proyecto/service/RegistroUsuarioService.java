package com.SCPAR.proyecto.service;

import com.SCPAR.proyecto.dto.RegistroUsuarioDTO;
import com.SCPAR.proyecto.model.CatCalle;
import com.SCPAR.proyecto.model.CuentaServicio;
import com.SCPAR.proyecto.model.Usuario;
import com.SCPAR.proyecto.repository.CatCalleRepository;
import com.SCPAR.proyecto.repository.CuentaServicioRepository;
import com.SCPAR.proyecto.repository.UsuarioRespository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistroUsuarioService {
    @Autowired
    private UsuarioRespository usuarioRespository;

    @Autowired
    private CuentaServicioRepository cuentaServicioRepository;

    @Autowired
    private CatCalleRepository catCalleRepository;

    //metodo para llenar el select de calles en el html
    public List<CatCalle> obtenerCatCalle() {
        return catCalleRepository.findAll();
    }

    //metodo transaccional para guardar ambos
    @Transactional
    public void registrarUsuario(RegistroUsuarioDTO dto) {

        // crear y llenar el modelo usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombres(dto.getNombres());
        nuevoUsuario.setApellidoPaterno(dto.getApellidoPaterno());
        nuevoUsuario.setApellidoMaterno(dto.getApellidoMaterno());
        nuevoUsuario.setFechaRegistro(dto.getFechaRegistro());

        //guardar el ususario (mysql le asigna un id automaticamente)
        Usuario usuarioGuardado = usuarioRespository.save(nuevoUsuario);
        // crear y llenar el modelo cuentaServicio
        CuentaServicio nuevaCuenta = new CuentaServicio();
        nuevaCuenta.setUsuario(usuarioGuardado); // se asigna el usuario que se acaba de guardar
        CatCalle calleSeleccionada = catCalleRepository.findById(dto.getIdCalle()).orElse(null);
        nuevaCuenta.setCalle(calleSeleccionada);

        nuevaCuenta.setNumeroCasa(dto.getNumeroCasa());
        nuevaCuenta.setCodigoPostal(dto.getCodigoPostal());
        nuevaCuenta.setIdServicio(dto.getIdServicio());
        nuevaCuenta.setDescuentoInapam(dto.getDescuentoInapam());

        nuevaCuenta.setEstatusCuenta(1);
        cuentaServicioRepository.save(nuevaCuenta);
    }
}
