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

    @Autowired
    private CuentaServicioRepository cuentaRepository;

    @GetMapping("/nuevo")
    public String mostrarFormularioRegistro(Model model) {
        if (!model.containsAttribute("registroDTO")) {
            model.addAttribute("registroDTO", new RegistroUsuarioDTO());
        }

        // Enviamos las calles
        model.addAttribute("listaCalles", registroUsuarioService.obtenerCatCalle());

        // <-- NUEVA LÍNEA: Enviamos los servicios -->
        model.addAttribute("listaServicios", registroUsuarioService.obtenerCatServicios());

        return "usuario-form";
    }

    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute("registroDTO") RegistroUsuarioDTO registroDTO, RedirectAttributes redirectAttributes) {

        // Validamos si es nulo o está en blanco (para Strings)
        if (registroDTO.getFolioTarjeta() == null || registroDTO.getFolioTarjeta().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("mensajeError", "El folio no debe estar vacío.");
            redirectAttributes.addFlashAttribute("registroDTO", registroDTO);
            return "redirect:/usuarios/nuevo";
        }

        // --- 1. EL ESCUDO ANTI-DUPLICADOS ---
        if (cuentaRepository.existsById(registroDTO.getFolioTarjeta())) {
            redirectAttributes.addFlashAttribute("mensajeError", "El folio " + registroDTO.getFolioTarjeta() + " ya está registrado en el sistema. Verifica la tarjeta.");
            redirectAttributes.addFlashAttribute("registroDTO", registroDTO);
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