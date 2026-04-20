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
    public String mostrarFormularioNuevaPersona(Model model, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        // REGLA DE NEGOCIO: Verificar si ya existe un administrador
        // Si la lista de administradores NO está vacía, significa que ya hay uno.
        if (!administradorService.listarTodas().isEmpty()) {
            // Mandamos el mensaje de error rojo a la pantalla de login
            redirectAttributes.addFlashAttribute("error", "El cupo está lleno. Ya existe un administrador registrado para este periodo.");
            return "redirect:/administrador/login";
        }

        model.addAttribute("administrador", new Administrador());
        return "administrador-form";
    }

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder; // Inyecta el encriptador

    @PostMapping
    public String guardarAdministrador(@ModelAttribute Administrador administrador, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes){

        // 1. Verificamos que no haya cupo lleno
        if (!administradorService.listarTodas().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Operación bloqueada: Ya existe un administrador registrado.");
            return "redirect:/administrador/login";
        }

        // 2. VALIDACIÓN DE CORREO: Revisamos que tenga formato texto@texto.com
        String regexCorreo = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if (administrador.getCorreo() == null || !administrador.getCorreo().matches(regexCorreo)) {
            redirectAttributes.addFlashAttribute("error", "El correo ingresado no es válido. Verifica que esté bien escrito (ejemplo: admin@gmail.com).");
            return "redirect:/administrador/nuevo";
        }

        // 3. VALIDACIÓN DE CONTRASEÑA: Revisamos los 4 requisitos
        String regexPassword = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$";
        if (administrador.getPassword() == null || !administrador.getPassword().matches(regexPassword)) {
            redirectAttributes.addFlashAttribute("error", "La contraseña es débil. Asegúrate de cumplir con los 4 requisitos que se muestran en la pantalla.");
            return "redirect:/administrador/nuevo";
        }

        // 4. Si pasa todas las pruebas, Encriptamos y guardamos
        String hash = passwordEncoder.encode(administrador.getPassword());
        administrador.setPassword(hash);
        administrador.setActivo(true);

        administradorService.guardarAdministrador(administrador);

        redirectAttributes.addFlashAttribute("mensajeExito", "¡Administrador registrado correctamente!");

        // CAMBIA ESTA LÍNEA PARA QUE VAYA AL LOGIN
        return "redirect:/administrador/login";
    }

    // 4. MOSTRAR LOGIN (GET) - Ruta: /administrador/login
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @GetMapping("/menu")
    public String mostrarMenu(@RequestParam(value = "login", required = false) String loginEstatus, Model model) {

        // Si la URL trae "?login=exito", mandamos el mensaje a la vista
        if ("exito".equals(loginEstatus)) {
            model.addAttribute("mensajeExito", "¡Sesión iniciada correctamente! Bienvenido(a) al menú.");
        }

        return "menu";
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
            return "redirect:/administrador/menu"; // Éxito: va a la lista
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

    @GetMapping("/pagos/nuevo")
    public String mostrarFormularioPago() {
        return "pago-form"; // Nombre del archivo HTML sin el .html
    }
}
