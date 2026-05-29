package com.SCPAR.proyecto.controller;

import com.SCPAR.proyecto.dto.EstadoCuentaDTO;
import com.SCPAR.proyecto.model.Pago;
import com.SCPAR.proyecto.repository.PagoRepository;
import com.SCPAR.proyecto.service.EstadoCuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class EstadoCuentaController {

    @Autowired
    private EstadoCuentaService estadoCuentaService;

    @Autowired
    private PagoRepository pagoRepository;

    // --- AQUÍ ESTÁ LA MAGIA ---
    // Ahora recibe un folio opcional. Si viene de la tabla, entra aquí.
    @GetMapping("/consultar")
    public String mostrarBuscador(@RequestParam(value = "folio", required = false) String folio, Model model) {

        // Si el folio NO es nulo y NO está vacío, buscamos la cuenta directo
        if (folio != null && !folio.trim().isEmpty()) {
            EstadoCuentaDTO datos = estadoCuentaService.obtenerDetalleEstado(folio);

            if (datos != null) {
                model.addAttribute("cuenta", datos);
                return "estado_cuenta_vista"; // Salta directo al resultado
            } else {
                model.addAttribute("error", "El folio " + folio + " no existe en el sistema.");
            }
        }

        // Si no mandaron folio (o si hubo error), mostramos el buscador normal
        return "buscar_folio";
    }

    // Este es el que se usa cuando escriben en la cajita y le dan al botón buscar
    @PostMapping("/buscar")
    public String procesarBusqueda(@RequestParam("folio") String folio, Model model) {
        EstadoCuentaDTO datos = estadoCuentaService.obtenerDetalleEstado(folio);

        if (datos != null) {
            model.addAttribute("cuenta", datos);
            return "estado_cuenta_vista";
        } else {
            model.addAttribute("error", "El folio " + folio + " no existe en el sistema.");
            return "buscar_folio";
        }
    }

    @GetMapping("/estado-cuenta/{folio}")
    public String verEstadoDirecto(@PathVariable String folio, Model model) {
        EstadoCuentaDTO datos = estadoCuentaService.obtenerDetalleEstado(folio);
        if (datos != null) {
            model.addAttribute("cuenta", datos);
            return "estado_cuenta_vista";
        }
        return "redirect:/?error=notfound";
    }

    @GetMapping("/historial/{folio}")
    public String verHistorial(@PathVariable String folio, Model model) {
        List<Pago> pagos = pagoRepository.findByCuenta_FolioTarjetaOrderByFechaPagoDesc(folio);

        model.addAttribute("pagos", pagos);
        model.addAttribute("folio", folio);

        return "historial_pagos";
    }
}