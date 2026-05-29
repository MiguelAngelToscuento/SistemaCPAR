package com.SCPAR.proyecto.controller;

import com.SCPAR.proyecto.service.BackupService;
import com.SCPAR.proyecto.repository.CuentaServicioRepository;
import com.SCPAR.proyecto.repository.PagoRepository; // <-- IMPORTAMOS TU REPOSITORIO DE PAGOS
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/administrador")
public class AdministradorController {

    @Autowired
    private AdministradorService administradorService;

    @Autowired
    private BackupService backupService;

    @Autowired
    private CuentaServicioRepository cuentaServicioRepository;

    // <-- INYECTAMOS TU REPOSITORIO DE PAGOS AQUÍ -->
    @Autowired
    private PagoRepository pagoRepository;

    // 1. LISTAR (GET)
    @GetMapping
    public String listarAdministradores(Model model) {
        model.addAttribute("administrador", administradorService.listarTodas());
        return "administrador-list";
    }

    @PostMapping("/backup/manual")
    public String hacerRespaldoManual(RedirectAttributes redirectAttributes) {
        boolean exito = backupService.ejecutarRespaldoManual();

        if (exito) {
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Respaldo manual creado correctamente! Guardado en el servidor.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Error: No se pudo generar el respaldo. Revisa los registros del sistema.");
        }

        return "redirect:/administrador/menu";
    }

    // 2. MOSTRAR FORMULARIO DE REGISTRO (GET)
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevaPersona(Model model, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (!administradorService.listarTodas().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El cupo está lleno. Ya existe un administrador registrado para este periodo.");
            return "redirect:/administrador/login";
        }

        model.addAttribute("administrador", new Administrador());
        return "administrador-form";
    }

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @PostMapping
    public String guardarAdministrador(@ModelAttribute Administrador administrador, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes){
        if (!administradorService.listarTodas().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Operación bloqueada: Ya existe un administrador registrado.");
            return "redirect:/administrador/login";
        }

        String regexCorreo = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if (administrador.getCorreo() == null || !administrador.getCorreo().matches(regexCorreo)) {
            redirectAttributes.addFlashAttribute("error", "El correo ingresado no es válido. Verifica que esté bien escrito (ejemplo: admin@gmail.com).");
            return "redirect:/administrador/nuevo";
        }

        String regexPassword = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$";
        if (administrador.getPassword() == null || !administrador.getPassword().matches(regexPassword)) {
            redirectAttributes.addFlashAttribute("error", "La contraseña es débil. Asegúrate de cumplir con los 4 requisitos que se muestran en la pantalla.");
            return "redirect:/administrador/nuevo";
        }

        String hash = passwordEncoder.encode(administrador.getPassword());
        administrador.setPassword(hash);
        administrador.setActivo(true);

        administradorService.guardarAdministrador(administrador);
        redirectAttributes.addFlashAttribute("mensajeExito", "¡Administrador registrado correctamente!");

        return "redirect:/administrador/login";
    }

    // 4. MOSTRAR LOGIN (GET)
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @GetMapping("/menu")
    public String mostrarMenu(@RequestParam(value = "login", required = false) String loginEstatus, Model model) {

        if ("exito".equals(loginEstatus)) {
            model.addAttribute("mensajeExito", "¡Sesión iniciada correctamente! Bienvenido(a) al menú.");
        }

        // --- MAGIA DEL DASHBOARD: Enviando datos a la vista ---

        // 1. Cuenta total de usuarios registrados
        long totalUsuarios = cuentaServicioRepository.count();
        model.addAttribute("totalUsuarios", totalUsuarios);

        // 2. CONTEO DE DEUDORES REAL (Llamamos a tu Query y vemos cuántos hay)
        int cantidadDeudores = pagoRepository.findCuentasConAdeudo().size();
        model.addAttribute("adeudosActivos", cantidadDeudores);

        return "menu";
    }

    // 5. PROCESAR LOGIN (POST)
    @PostMapping("/login")
    public String procesarLogin(@RequestParam("username") String correo,
                                @RequestParam("password") String password,
                                jakarta.servlet.http.HttpSession session,
                                Model model) {

        Administrador admin = administradorService.listarTodas().stream()
                .filter(a -> a.getCorreo().equals(correo) && a.getPassword().equals(password))
                .findFirst()
                .orElse(null);

        if (admin != null) {
            session.setAttribute("usuarioLogueado", admin);
            return "redirect:/administrador/menu";
        } else {
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "login";
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
        return "pago-form";
    }
}