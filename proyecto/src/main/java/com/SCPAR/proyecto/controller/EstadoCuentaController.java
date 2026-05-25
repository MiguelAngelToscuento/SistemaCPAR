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

    @GetMapping("/consultar")
    public String mostrarBuscador() {
        return "buscar_folio";
    }

    @PostMapping("/buscar")
    public String procesarBusqueda(@RequestParam("folio") String folio, Model model) { // Cambiado a String
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
    public String verEstadoDirecto(@PathVariable String folio, Model model) { // Cambiado a String
        EstadoCuentaDTO datos = estadoCuentaService.obtenerDetalleEstado(folio);
        if (datos != null) {
            model.addAttribute("cuenta", datos);
            return "estado_cuenta_vista";
        }
        return "redirect:/?error=notfound";
    }

    @GetMapping("/historial/{folio}")
    public String verHistorial(@PathVariable String folio, Model model) { // Cambiado a String
        List<Pago> pagos = pagoRepository.findByCuenta_FolioTarjetaOrderByFechaPagoDesc(folio);

        model.addAttribute("pagos", pagos);
        model.addAttribute("folio", folio);

        return "historial_pagos";
    }
}