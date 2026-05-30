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

        model.addAttribute("listaCalles", registroUsuarioService.obtenerCatCalle());
        model.addAttribute("listaServicios", registroUsuarioService.obtenerCatServicios());

        return "usuario-form";
    }

    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute("registroDTO") RegistroUsuarioDTO registroDTO, RedirectAttributes redirectAttributes) {

        if (registroDTO.getFolioTarjeta() == null || registroDTO.getFolioTarjeta().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("mensajeError", "El folio no debe estar vacío.");
            redirectAttributes.addFlashAttribute("registroDTO", registroDTO);
            return "redirect:/usuarios/nuevo";
        }

        if (cuentaRepository.existsById(registroDTO.getFolioTarjeta())) {
            redirectAttributes.addFlashAttribute("mensajeError", "El folio " + registroDTO.getFolioTarjeta() + " ya está registrado en el sistema. Verifica la tarjeta.");
            redirectAttributes.addFlashAttribute("registroDTO", registroDTO);
            return "redirect:/usuarios/nuevo";
        } else {
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

    @GetMapping("/editar/{folio}")
    public String editarUsuario(@PathVariable String folio, Model model, RedirectAttributes redirectAttributes) {
        com.SCPAR.proyecto.model.CuentaServicio cuenta = cuentaRepository.findById(folio).orElse(null);

        if (cuenta == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "El usuario no existe.");
            return "redirect:/consultar";
        }

        // --- NUEVO CANDADO: Si está suspendida, lo rebotamos ---
        if (cuenta.getEstatusCuenta() != null && cuenta.getEstatusCuenta() == 0) {
            redirectAttributes.addFlashAttribute("mensajeError", "Acción denegada: La cuenta está SUSPENDIDA. Reactívala para poder editar su información.");
            return "redirect:/estado-cuenta/" + folio;
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

        // --- PRECargar LA FECHA HISTÓRICA AL EDITAR ---
        dto.setFechaUltimoPago(cuenta.getFechaUltimoPago());

        model.addAttribute("registroDTO", dto);
        model.addAttribute("listaCalles", registroUsuarioService.obtenerCatCalle());
        model.addAttribute("listaServicios", registroUsuarioService.obtenerCatServicios());
        model.addAttribute("esEdicion", true);

        return "usuario-form";
    }

    @PostMapping("/actualizar")
    public String actualizarUsuario(@ModelAttribute("registroDTO") RegistroUsuarioDTO registroDTO, RedirectAttributes redirectAttributes) {
        try {
            registroUsuarioService.actualizarUsuario(registroDTO);
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Los datos del cliente se actualizaron correctamente!");
            return "redirect:/estado-cuenta/" + registroDTO.getFolioTarjeta();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al actualizar: " + e.getMessage());
            return "redirect:/usuarios/editar/" + registroDTO.getFolioTarjeta();
        }
    }
    // Agrega este método a tu UsuarioController.java
    @GetMapping("/ultimos-registros")
    public String mostrarUltimosRegistros(@org.springframework.web.bind.annotation.RequestParam(value = "idCalle", required = false) Integer idCalle, Model model) {
        // Siempre enviamos la lista de calles para el menú desplegable
        model.addAttribute("listaCalles", registroUsuarioService.obtenerCatCalle());

        // Si el usuario ya seleccionó una calle, buscamos sus registros
        if (idCalle != null) {
            model.addAttribute("idCalleSeleccionada", idCalle);
            model.addAttribute("listaCuentas", registroUsuarioService.obtenerCuentasPorCalle(idCalle));
        }

        return "ultimos-registros";
    }
}