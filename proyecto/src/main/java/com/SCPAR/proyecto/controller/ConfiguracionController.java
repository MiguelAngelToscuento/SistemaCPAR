package com.SCPAR.proyecto.controller;

import com.SCPAR.proyecto.model.CatCalle;
import com.SCPAR.proyecto.model.CatTipoServicio;
import com.SCPAR.proyecto.repository.CatCalleRepository;
import com.SCPAR.proyecto.repository.CatTipoServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/configuracion")
public class ConfiguracionController {

    @Autowired
    private CatCalleRepository catCalleRepository;

    @Autowired
    private CatTipoServicioRepository catTipoServicioRepository;

    // 1. MOSTRAR PANEL DE CONFIGURACIÓN
    @GetMapping
    public String mostrarPanel(Model model) {
        model.addAttribute("listaCalles", catCalleRepository.findAll());
        model.addAttribute("listaServicios", catTipoServicioRepository.findAll());
        return "configuracion";
    }

    // 2. GUARDAR NUEVA CALLE
    @PostMapping("/calle/guardar")
    public String guardarCalle(@RequestParam("nombreCalle") String nombreCalle, RedirectAttributes redirectAttributes) {
        if (nombreCalle == null || nombreCalle.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorCalle", "El nombre de la calle no puede estar vacío.");
            return "redirect:/configuracion";
        }

        // Evitar duplicados exactos
        if (catCalleRepository.existsByNombreCalle(nombreCalle.trim())) {
            redirectAttributes.addFlashAttribute("errorCalle", "La calle '" + nombreCalle + "' ya se encuentra registrada.");
            return "redirect:/configuracion";
        }

        try {
            CatCalle nuevaCalle = new CatCalle();
            nuevaCalle.setNombreCalle(nombreCalle.trim());
            catCalleRepository.save(nuevaCalle);
            redirectAttributes.addFlashAttribute("exitoCalle", "¡Calle registrada correctamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorCalle", "Error al guardar la calle: " + e.getMessage());
        }

        return "redirect:/configuracion";
    }

    // 3. GUARDAR O MODIFICAR PRECIO DE SERVICIO
    @PostMapping("/servicio/guardar")
    public String guardarOActualizarServicio(
            @RequestParam("nombreServicio") String nombreServicio,
            @RequestParam("tarifa") BigDecimal tarifa,
            RedirectAttributes redirectAttributes) {

        if (nombreServicio == null || nombreServicio.trim().isEmpty() || tarifa == null) {
            redirectAttributes.addFlashAttribute("errorServicio", "Todos los campos del servicio son obligatorios.");
            return "redirect:/configuracion";
        }

        if (tarifa.compareTo(BigDecimal.ZERO) < 0) {
            redirectAttributes.addFlashAttribute("errorServicio", "La tarifa no puede ser un monto negativo.");
            return "redirect:/configuracion";
        }

        try {
            // Buscamos si ya existe un servicio con ese nombre exacto para actualizarlo
            CatTipoServicio servicioExistente = catTipoServicioRepository.findByNombreServicio(nombreServicio.trim());

            if (servicioExistente != null) {
                // Si existe, modificamos su precio
                servicioExistente.setTarifa(tarifa);
                catTipoServicioRepository.save(servicioExistente);
                redirectAttributes.addFlashAttribute("exitoServicio", "¡Precio del servicio '" + nombreServicio + "' actualizado con éxito!");
            } else {
                // Si no existe, creamos el nuevo tipo de servicio (Ej. Fraccionamiento)
                CatTipoServicio nuevoServicio = new CatTipoServicio();
                nuevoServicio.setNombreServicio(nombreServicio.trim());
                nuevoServicio.setTarifa(tarifa);
                catTipoServicioRepository.save(nuevoServicio);
                redirectAttributes.addFlashAttribute("exitoServicio", "¡Nuevo servicio registrado con éxito!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorServicio", "Error al procesar el servicio: " + e.getMessage());
        }

        return "redirect:/configuracion";
    }
}