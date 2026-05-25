package com.SCPAR.proyecto.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarCorreoRecuperacion(String destinatario, String enlaceRecuperacion) {
        SimpleMailMessage mensaje = new SimpleMailMessage();

        mensaje.setTo(destinatario);
        mensaje.setSubject("Recuperación de Contraseña - Presidencia de Comunidad Texcalac");
        mensaje.setText("Hola,\n\n" +
                "Has solicitado restablecer tu contraseña en el sistema. Haz clic en el siguiente enlace para crear una nueva:\n\n" +
                enlaceRecuperacion + "\n\n" +
                "Importante: Este enlace es seguro y expirará en 15 minutos.\n" +
                "Si no solicitaste este cambio, ignora este correo. Tu cuenta está segura.\n\n" +
                "Atentamente,\n" +
                "Sistema de Agua - Santa María Texcalac");

        mailSender.send(mensaje);
    }
}