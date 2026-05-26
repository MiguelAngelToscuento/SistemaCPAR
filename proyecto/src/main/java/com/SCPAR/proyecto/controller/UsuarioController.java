package com.SCPAR.proyecto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.SCPAR.proyecto.dto.RegistroUsuarioDTO;
import com.SCPAR.proyecto.repository.CuentaServicioRepository;
import com.SCPAR.proyecto.service.RegistroUsuarioService;

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

    // 1. Mostrar el formulario con los datos pre-cargados
    @GetMapping("/editar/{folio}")
    public String editarUsuario(@PathVariable String folio, Model model, RedirectAttributes redirectAttributes) {
        // Buscamos la cuenta en la base de datos
        com.SCPAR.proyecto.model.CuentaServicio cuenta = cuentaRepository.findById(folio).orElse(null);

        if (cuenta == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "El usuario no existe.");
            return "redirect:/consultar";
        }

        // Pasamos los datos al DTO para que el formulario los entienda
        RegistroUsuarioDTO dto = new RegistroUsuarioDTO();
        dto.setFolioTarjeta(cuenta.getFolioTarjeta());
        dto.setNombres(cuenta.getNombres());
        dto.setApellidoPaterno(cuenta.getApellidoPaterno());
        dto.setApellidoMaterno(cuenta.getApellidoMaterno());
        dto.setFechaRegistro(cuenta.getFechaRegistro());
        dto.setIdCalle(cuenta.getCalle() != null ? cuenta.getCalle().getIdCalle() : null);
        dto.setNumeroExterior(cuenta.getNumeroExterior());
        dto.setNumeroInterior(cuenta.getNumeroInterior());
        dto.setCodigoPostal(cuenta.getCodigoPostal());
        dto.setIdServicio(cuenta.getIdServicio());
        dto.setDescuentoInapam(cuenta.getDescuentoInapam());

        model.addAttribute("registroDTO", dto);
        model.addAttribute("listaCalles", registroUsuarioService.obtenerCatCalle());
        model.addAttribute("listaServicios", registroUsuarioService.obtenerCatServicios());
        model.addAttribute("esEdicion", true); // <-- Bandera muy importante para la vista

        return "usuario-form";
    }

    // 2. Guardar las modificaciones
    @PostMapping("/actualizar")
    public String actualizarUsuario(@ModelAttribute("registroDTO") RegistroUsuarioDTO registroDTO, RedirectAttributes redirectAttributes) {
        try {
            registroUsuarioService.actualizarUsuario(registroDTO);
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Los datos del cliente se actualizaron correctamente!");
            // Redirigimos de vuelta al estado de cuenta de ese usuario
            return "redirect:/estado-cuenta/" + registroDTO.getFolioTarjeta();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al actualizar: " + e.getMessage());
            return "redirect:/usuarios/editar/" + registroDTO.getFolioTarjeta();
        }
    }
}
