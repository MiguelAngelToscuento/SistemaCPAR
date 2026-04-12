package com.SCPAR.proyecto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/pagos")
public class PagoController {

    @GetMapping("/realizar")
    public String mostrarPantallaBusqueda() {
        return "pago-busqueda"; // Nombre del HTML que crearemos
    }

    @PostMapping("/buscar")
    public String buscarCuenta(@RequestParam("folio") String folio, Model model) {
        // Por ahora, solo simularemos que buscamos. 
        // Aquí es donde en el futuro llamarás a tu servicio.
        model.addAttribute("folio", folio);
        return "pago-busqueda";
    }
}
