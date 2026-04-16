package com.SCPAR.proyecto.controller;

import com.SCPAR.proyecto.dto.EstadoCuentaDTO;
import com.SCPAR.proyecto.service.EstadoCuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EstadoCuentaController {

    @Autowired
    private EstadoCuentaService estadoCuentaService;

    // 1. Mostrar la página de inicio con la barra de búsqueda
    @GetMapping("/consultar")
    public String mostrarBuscador() {
        return "buscar_folio";
    }

    // 2. Procesar el formulario de búsqueda
    @PostMapping("/buscar")
    public String procesarBusqueda(@RequestParam("folio") Integer folio, Model model) {
        EstadoCuentaDTO datos = estadoCuentaService.obtenerDetalleEstado(folio);
        
        if (datos != null) {
            model.addAttribute("cuenta", datos);
            return "estado_cuenta_vista"; // Nos lleva a la info si existe
        } else {
            model.addAttribute("error", "El folio " + folio + " no existe en el sistema.");
            return "buscar_folio"; // Regresa al buscador con error
        }
    }

    // Mantener acceso directo por URL si lo necesitas
    @GetMapping("/estado-cuenta/{folio}")
    public String verEstadoDirecto(@PathVariable Integer folio, Model model) {
        EstadoCuentaDTO datos = estadoCuentaService.obtenerDetalleEstado(folio);
        if (datos != null) {
            model.addAttribute("cuenta", datos);
            return "estado_cuenta_vista";
        }
        return "redirect:/?error=notfound";
    }
}