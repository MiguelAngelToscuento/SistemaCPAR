package com.SCPAR.proyecto.controller;


import com.SCPAR.proyecto.model.Administrador;
import com.SCPAR.proyecto.service.AdministradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/administrador")

public class AdministradorController {
    @Autowired
    private AdministradorService administradorService;

    @GetMapping
    public String listarAdministradores(Model model){
        model.addAttribute("administrador", administradorService.listarTodas());
        return "administrador-list";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevaPersona(Model model){
        model.addAttribute("administrador", new Administrador());
        return "administrador-form";
    }

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder; // Inyecta el encriptador

    @PostMapping
    public String guardarAdministrador(@ModelAttribute Administrador administrador){
        // Encriptar la contraseña antes de guardar
        String hash = passwordEncoder.encode(administrador.getPassword());
        administrador.setPassword(hash);

        administradorService.guardarAdministrador(administrador);
        return "redirect:/"; // Regresamos al index después de registrar
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login"; // Mostrará tu archivo login.html
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarPersona(@PathVariable Integer id, Model model){
        model.addAttribute("administrador", administradorService.obtenderAdministradorById(id));
        return "administrador-form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarAdministrador(@PathVariable Integer id){
        administradorService.eliminarAdministrador(id);
        return "redirect:/administrador";
    }
}
