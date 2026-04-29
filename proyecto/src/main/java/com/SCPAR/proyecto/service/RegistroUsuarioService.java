package com.SCPAR.proyecto.service;

import com.SCPAR.proyecto.dto.RegistroUsuarioDTO;
import com.SCPAR.proyecto.model.CatCalle;
import com.SCPAR.proyecto.model.CuentaServicio;
import com.SCPAR.proyecto.repository.CatCalleRepository;
import com.SCPAR.proyecto.repository.CuentaServicioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistroUsuarioService {

    @Autowired
    private CuentaServicioRepository cuentaServicioRepository;

    @Autowired
    private CatCalleRepository catCalleRepository;

    public List<CatCalle> obtenerCatCalle() {
        return catCalleRepository.findAll();
    }

    @Transactional
    public void registrarUsuario(RegistroUsuarioDTO dto) {

        CuentaServicio nuevaCuenta = new CuentaServicio();

        // 1. AQUI TOMAMOS EL FOLIO MANUAL
        nuevaCuenta.setFolioTarjeta(dto.getFolioTarjeta());

        // 2. Datos personales
        nuevaCuenta.setNombres(dto.getNombres());
        nuevaCuenta.setApellidoPaterno(dto.getApellidoPaterno());
        nuevaCuenta.setApellidoMaterno(dto.getApellidoMaterno());
        nuevaCuenta.setFechaRegistro(dto.getFechaRegistro());

        // 3. Dirección y Servicio
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