package com.SCPAR.proyecto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.SCPAR.proyecto.model.Administrador;
import com.SCPAR.proyecto.service.AdministradorService;

@Controller
@RequestMapping("/administrador")
public class AdministradorController {

    @Autowired
    private AdministradorService administradorService;

    // 1. LISTAR (GET)
    @GetMapping
    public String listarAdministradores(Model model) {
        model.addAttribute("administrador", administradorService.listarTodas());
        return "administrador-list";
    }

    // 2. MOSTRAR FORMULARIO DE REGISTRO (GET)
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevaPersona(Model model) {
        model.addAttribute("administrador", new Administrador());
        return "administrador-form";
    }

    // 3. GUARDAR REGISTRO (POST) - Ruta: /administrador
    @PostMapping
    public String guardarAdministrador(@ModelAttribute Administrador administrador) {
        administradorService.guardarAdministrador(administrador);
        return "redirect:/administrador/login"; // Al registrar, lo mandamos a loguearse
    }

    // 4. MOSTRAR LOGIN (GET) - Ruta: /administrador/login
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    // 5. PROCESAR LOGIN (POST) - Ruta: /administrador/login
    // Esta es la parte que faltaba y que debe tener una ruta distinta al guardado
    @PostMapping("/login")
    public String procesarLogin(@RequestParam("username") String correo,
            @RequestParam("password") String password,
            jakarta.servlet.http.HttpSession session,
            Model model) {

        // Buscamos si existe el admin con ese correo
        Administrador admin = administradorService.listarTodas().stream()
                .filter(a -> a.getCorreo().equals(correo) && a.getPassword().equals(password))
                .findFirst()
                .orElse(null);

        if (admin != null) {
            session.setAttribute("usuarioLogueado", admin);
            return "redirect:/administrador"; // Éxito: va a la lista
        } else {
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "login"; // Falla: regresa al login con error
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarPersona(@PathVariable Integer id, Model model) {
        model.addAttribute("administrador", administradorService.obtenderAdministradorById(id));
        return "administrador-form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarAdministrador(@PathVariable Integer id) {
        administradorService.eliminarAdministrador(id);
        return "redirect:/administrador";
    }
}
