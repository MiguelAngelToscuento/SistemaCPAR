package com.SCPAR.proyecto.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; // <-- NUEVO

import com.SCPAR.proyecto.dto.RegistroUsuarioDTO;
import com.SCPAR.proyecto.model.CatCalle;
import com.SCPAR.proyecto.model.CatTipoServicio; // <-- NUEVO
import com.SCPAR.proyecto.model.CuentaServicio;
import com.SCPAR.proyecto.repository.CatCalleRepository;
import com.SCPAR.proyecto.repository.CatTipoServicioRepository;
import com.SCPAR.proyecto.repository.CuentaServicioRepository;

import jakarta.transaction.Transactional;

@Service
public class RegistroUsuarioService {

    @Autowired
    private CuentaServicioRepository cuentaServicioRepository;

    @Autowired
    private CatCalleRepository catCalleRepository;

    @Autowired
    private CatTipoServicioRepository catTipoServicioRepository; // <-- NUEVO

    public List<CatCalle> obtenerCatCalle() {
        return catCalleRepository.findAll();
    }

    // <-- NUEVO MÉTODO -->
    public List<CatTipoServicio> obtenerCatServicios() {
        return catTipoServicioRepository.findAll();
    }

    @Transactional
    public void registrarUsuario(RegistroUsuarioDTO dto) {
        CuentaServicio nuevaCuenta = new CuentaServicio();

        nuevaCuenta.setFolioTarjeta(dto.getFolioTarjeta());
        nuevaCuenta.setNombres(dto.getNombres());
        nuevaCuenta.setApellidoPaterno(dto.getApellidoPaterno());
        nuevaCuenta.setApellidoMaterno(dto.getApellidoMaterno());
        nuevaCuenta.setFechaRegistro(dto.getFechaRegistro());

        CatCalle calleSeleccionada = catCalleRepository.findById(dto.getIdCalle()).orElse(null);
        nuevaCuenta.setCalle(calleSeleccionada);

        nuevaCuenta.setNumeroExterior(dto.getNumeroExterior());
        nuevaCuenta.setNumeroInterior(dto.getNumeroInterior());

        nuevaCuenta.setCodigoPostal(dto.getCodigoPostal());
        nuevaCuenta.setIdServicio(dto.getIdServicio());
        nuevaCuenta.setDescuentoInapam(dto.getDescuentoInapam());
        nuevaCuenta.setEstatusCuenta(1);

        cuentaServicioRepository.save(nuevaCuenta);
    }

    @Transactional
    public void actualizarUsuario(RegistroUsuarioDTO dto) {
        CuentaServicio cuentaExistente = cuentaServicioRepository.findById(dto.getFolioTarjeta())
                .orElseThrow(() -> new RuntimeException("La cuenta no existe"));

        cuentaExistente.setNombres(dto.getNombres());
        cuentaExistente.setApellidoPaterno(dto.getApellidoPaterno());
        cuentaExistente.setApellidoMaterno(dto.getApellidoMaterno());
        // Si no quieres que cambien la fecha de registro original, puedes quitar la siguiente línea
        cuentaExistente.setFechaRegistro(dto.getFechaRegistro());

        CatCalle calleSeleccionada = catCalleRepository.findById(dto.getIdCalle()).orElse(null);
        cuentaExistente.setCalle(calleSeleccionada);

        cuentaExistente.setNumeroExterior(dto.getNumeroExterior());
        cuentaExistente.setNumeroInterior(dto.getNumeroInterior());
        cuentaExistente.setCodigoPostal(dto.getCodigoPostal());
        cuentaExistente.setIdServicio(dto.getIdServicio());
        cuentaExistente.setDescuentoInapam(dto.getDescuentoInapam());

        // Se guarda la actualización
        cuentaServicioRepository.save(cuentaExistente);
    }

}
