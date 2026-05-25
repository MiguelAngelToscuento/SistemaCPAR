package com.SCPAR.proyecto.controller;

import com.SCPAR.proyecto.model.Administrador;
import com.SCPAR.proyecto.model.PasswordResetToken;
import com.SCPAR.proyecto.repository.AdministradorRepository;
import com.SCPAR.proyecto.repository.PasswordResetTokenRepository;
import com.SCPAR.proyecto.service.EmailService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.UUID;

@Controller
public class PasswordResetController {

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 1. Mostrar la pantalla para introducir el correo
    @GetMapping("/olvide-password")
    public String mostrarFormularioOlvidePassword() {
        return "olvide-password";
    }

    // 2. Procesar el formulario cuando le dan a "Enviar enlace"
    @PostMapping("/olvide-password")
    @Transactional
    public String procesarOlvidePassword(@RequestParam("correo") String correo, RedirectAttributes redirectAttributes) {

        Administrador admin = administradorRepository.findByCorreo(correo);

        if (admin == null) {
            redirectAttributes.addFlashAttribute("mensaje", "Si el correo está registrado, te enviaremos un enlace de recuperación.");
            return "redirect:/olvide-password";
        }

        // CORREGIDO: Usando getId() y deleteByAdministrador_Id()
        tokenRepository.deleteByAdministrador_Id(admin.getId());

        String tokenString = UUID.randomUUID().toString();

        PasswordResetToken miToken = new PasswordResetToken();
        miToken.setToken(tokenString);
        miToken.setAdministrador(admin);
        miToken.setFechaExpiracion(LocalDateTime.now().plusMinutes(15));

        tokenRepository.save(miToken);

        String enlace = "http://localhost:8080/restablecer-password?token=" + tokenString;

        try {
            emailService.enviarCorreoRecuperacion(admin.getCorreo(), enlace);
            redirectAttributes.addFlashAttribute("mensajeExito", "Si el correo está registrado, te enviaremos un enlace de recuperación. Revisa tu bandeja de entrada o Spam.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Hubo un problema de conexión enviando el correo. Intenta de nuevo más tarde.");
        }

        return "redirect:/olvide-password";
    }

    // 3. Atrapa el clic del usuario cuando abre su correo
    @GetMapping("/restablecer-password")
    public String mostrarFormularioRestablecer(@RequestParam("token") String token, Model model, RedirectAttributes redirectAttributes) {

        PasswordResetToken miToken = tokenRepository.findByToken(token).orElse(null);

        if (miToken == null || miToken.estaExpirado()) {
            redirectAttributes.addFlashAttribute("error", "El enlace de recuperación es inválido o ya expiró. Por favor, solicita uno nuevo.");
            return "redirect:/administrador/login";
        }

        model.addAttribute("token", token);
        return "reestablecer-password";
    }

    // 4. Procesa la contraseña nueva y la guarda en la BD
    @PostMapping("/restablecer-password")
    @Transactional
    public String procesarNuevaPassword(@RequestParam("token") String token,
                                        @RequestParam("password") String password,
                                        RedirectAttributes redirectAttributes) {

        PasswordResetToken miToken = tokenRepository.findByToken(token).orElse(null);

        if (miToken == null || miToken.estaExpirado()) {
            redirectAttributes.addFlashAttribute("error", "El enlace de recuperación es inválido o ya expiró.");
            return "redirect:/administrador/login";
        }

        Administrador admin = miToken.getAdministrador();

        admin.setPassword(passwordEncoder.encode(password));
        administradorRepository.save(admin);

        tokenRepository.delete(miToken);

        redirectAttributes.addFlashAttribute("mensajeExito", "¡Tu contraseña ha sido actualizada correctamente! Ya puedes iniciar sesión.");
        return "redirect:/administrador/login";
    }
}