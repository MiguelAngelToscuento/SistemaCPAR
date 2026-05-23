package com.SCPAR.proyecto.service;

import com.SCPAR.proyecto.dto.EstadoCuentaDTO;
import com.SCPAR.proyecto.model.CatTipoServicio;
import com.SCPAR.proyecto.model.CuentaServicio;
import com.SCPAR.proyecto.repository.CatTipoServicioRepository;
import com.SCPAR.proyecto.repository.CuentaServicioRepository;
import com.SCPAR.proyecto.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EstadoCuentaService {

    @Autowired
    private CuentaServicioRepository cuentaRepo;

    @Autowired
    private PagoRepository pagoRepo;

    @Autowired
    private CatTipoServicioRepository servicioRepo;

    public EstadoCuentaDTO obtenerDetalleEstado(String folio) {
        CuentaServicio cuenta = cuentaRepo.findById(folio).orElse(null);
        if (cuenta == null) return null;

        EstadoCuentaDTO dto = new EstadoCuentaDTO();
        dto.setFolioTarjeta(cuenta.getFolioTarjeta());
        dto.setNombreTitular(cuenta.getNombres() + " " + cuenta.getApellidoPaterno() + " " + (cuenta.getApellidoMaterno() != null ? cuenta.getApellidoMaterno() : ""));

        // <-- LÓGICA INTELIGENTE PARA LA DIRECCIÓN -->
        if(cuenta.getCalle() != null) {
            String domicilio = cuenta.getCalle().getNombreCalle() + " #" + cuenta.getNumeroExterior();
            // Si tiene número interior, lo agregamos
            if (cuenta.getNumeroInterior() != null && !cuenta.getNumeroInterior().trim().isEmpty()) {
                domicilio += " Int. " + cuenta.getNumeroInterior();
            }
            dto.setDomicilioCompleto(domicilio);
        }

        dto.setCodigoPostal(cuenta.getCodigoPostal());

        if(cuenta.getIdServicio() != null) {
            CatTipoServicio servicio = servicioRepo.findById(cuenta.getIdServicio()).orElse(null);
            if (servicio != null) {
                dto.setTipoServicio(servicio.getNombreServicio());
                double tarifa = servicio.getTarifa().doubleValue();
                int meses = 1;

                double deuda = tarifa * meses;

                if (cuenta.getDescuentoInapam() != null && cuenta.getDescuentoInapam()) {
                    deuda = deuda - 20;
                }

                dto.setMesesPendientes(meses);
                dto.setMontoTotalDeuda(deuda);
            }
        }

        dto.setDescuentoInapamStr(cuenta.getDescuentoInapam() != null && cuenta.getDescuentoInapam() ? "Si" : "No");
        dto.setUltimaFechaPago(pagoRepo.findUltimaFechaByFolio(folio));

        return dto;
    }
}