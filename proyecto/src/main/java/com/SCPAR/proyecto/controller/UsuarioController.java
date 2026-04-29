package com.SCPAR.proyecto.controller;

import com.SCPAR.proyecto.dto.RegistroUsuarioDTO;
import com.SCPAR.proyecto.repository.CuentaServicioRepository;
import com.SCPAR.proyecto.service.RegistroUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private RegistroUsuarioService registroUsuarioService;

    // consultar los folios de la base de datos directamente con la clase repository
    @Autowired
    private CuentaServicioRepository cuentaRepository;

    @GetMapping("/nuevo")
    public String mostrarFormularioRegistro(Model model) {
        if (!model.containsAttribute("registroDTO")) {
            model.addAttribute("registroDTO", new RegistroUsuarioDTO());
        }
        model.addAttribute("listaCalles", registroUsuarioService.obtenerCatCalle());

        return "usuario-form";
    }

    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute("registroDTO") RegistroUsuarioDTO registroDTO, RedirectAttributes redirectAttributes) {

        //evitar que sea negativo el folio
        if (registroDTO.getFolioTarjeta() <0){
            redirectAttributes.addFlashAttribute("mensajeError", "El folio no debe ser un numero negativo");
            redirectAttributes.addFlashAttribute("registroDTO", registroDTO);
            return "redirect:/usuarios/nuevo";
        }

        // --- 1. EL ESCUDO ANTI-DUPLICADOS ---
        // Preguntamos a MySQL si el folio que escribieron ya existe en la tabla
        if (cuentaRepository.existsById(registroDTO.getFolioTarjeta())) {

            // Si ya existe, disparamos el mensaje rojo
            redirectAttributes.addFlashAttribute("mensajeError", "El folio " + registroDTO.getFolioTarjeta() + " ya está registrado en el sistema. Verifica la tarjeta.");

            // Guardamos lo que ya había escrito para que no se borre el formulario
            redirectAttributes.addFlashAttribute("registroDTO", registroDTO);

            // Lo rebotamos al formulario
            return "redirect:/usuarios/nuevo";
        } else {
            // --- 2. FLUJO NORMAL DE GUARDADO ---
            try {
                registroUsuarioService.registrarUsuario(registroDTO);
                redirectAttributes.addFlashAttribute("mensajeExito", "¡El usuario se registró correctamente en el sistema!");
                return "redirect:/administrador/menu";

            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("mensajeError", "Error al guardar en la Base de Datos: " + e.getMessage());
                redirectAttributes.addFlashAttribute("registroDTO", registroDTO);
                return "redirect:/usuarios/nuevo";
            }
        }



    }
}