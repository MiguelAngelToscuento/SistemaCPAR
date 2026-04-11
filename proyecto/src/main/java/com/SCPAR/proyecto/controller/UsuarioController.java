package com.SCPAR.proyecto.controller;

import com.SCPAR.proyecto.dto.RegistroUsuarioDTO;
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

    @GetMapping("/nuevo")
    public String mostrarFormularioRegistro(Model model) {
        // Solo mandamos el DTO si no existe uno ya (para no borrar lo que el usuario escribió si hay error)
        if (!model.containsAttribute("registroDTO")) {
            model.addAttribute("registroDTO", new RegistroUsuarioDTO());
        }
        model.addAttribute("listaCalles", registroUsuarioService.obtenerCatCalle());

        return "usuario-form";
    }

    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute("registroDTO") RegistroUsuarioDTO registroDTO, RedirectAttributes redirectAttributes) {
        try {
            // Intentamos guardar en la Base de Datos
            registroUsuarioService.registrarUsuario(registroDTO);

            // Si la línea de arriba funciona, mandamos mensaje de ÉXITO y redirigimos al menú ( / )
            redirectAttributes.addFlashAttribute("mensajeExito", "¡El usuario se registró correctamente en el sistema!");
            return "redirect:/administrador/menu";

        } catch (Exception e) {
            // Si MySQL rechaza el registro, atrapamos el error, mandamos mensaje de ERROR y lo regresamos al formulario
            redirectAttributes.addFlashAttribute("mensajeError", "Error al guardar en la Base de Datos: " + e.getMessage());

            // Conservamos los datos que escribió para que no tenga que llenar todo de nuevo
            redirectAttributes.addFlashAttribute("registroDTO", registroDTO);
            return "redirect:/usuarios/nuevo";
        }
    }
}