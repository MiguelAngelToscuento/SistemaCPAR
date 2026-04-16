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

    public EstadoCuentaDTO obtenerDetalleEstado(Integer folio) {
        CuentaServicio cuenta = cuentaRepo.findById(folio).orElse(null);
        if (cuenta == null) return null;

        EstadoCuentaDTO dto = new EstadoCuentaDTO();
        dto.setFolioTarjeta(cuenta.getFolioTarjeta());
        dto.setNombreTitular(cuenta.getNombres() + " " + cuenta.getApellidoPaterno() + " " + (cuenta.getApellidoMaterno() != null ? cuenta.getApellidoMaterno() : ""));
        
        // Uso de cuenta.getCalle() según tu entidad
        if(cuenta.getCalle() != null) {
            dto.setDomicilioCompleto(cuenta.getCalle().getNombreCalle() + " #" + cuenta.getNumeroCasa());
        }
        dto.setCodigoPostal(cuenta.getCodigoPostal());
        
        // Buscamos el servicio por el idServicio que tiene la cuenta
        if(cuenta.getIdServicio() != null) {
            CatTipoServicio servicio = servicioRepo.findById(cuenta.getIdServicio()).orElse(null);
            if (servicio != null) {
                dto.setTipoServicio(servicio.getNombreServicio());
                double tarifa = servicio.getTarifa().doubleValue();
                int meses = 1; // Valor de ejemplo
                dto.setMesesPendientes(meses);
                dto.setMontoTotalDeuda(tarifa * meses);
            }
        }
        
        // El campo es Boolean
        dto.setDescuentoInapamStr(cuenta.getDescuentoInapam() != null && cuenta.getDescuentoInapam() ? "Si" : "No");
        
        // Busca la fecha en PagoRepository (usando p.cuenta.folioTarjeta)
        dto.setUltimaFechaPago(pagoRepo.findUltimaFechaByFolio(folio));

        return dto;
    }
}