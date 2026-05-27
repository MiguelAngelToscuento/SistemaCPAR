package com.SCPAR.proyecto.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class BackupService {

    // Spring Boot lee estas variables automáticamente desde el application.properties
    @Value("${backups.ruta-carpeta}")
    private String rutaCarpeta;

    @Value("${backups.comando-dump}")
    private String rutaMysqlDump;
    //@Scheduled(cron = "0 * * * * *")
    @Scheduled(cron = "0 0 2 */15 * *")
    public void crearRespaldoAutomatico() {
        File carpeta = new File(rutaCarpeta);

        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"));
        String nombreArchivo = "respaldo_sistema_agua_" + fechaActual + ".sql";
        File archivoSalida = new File(carpeta, nombreArchivo);

        String usuario = "root";
        String password = "7585ToManf";
        String baseDatos = "sistema_agua_texcalac";

        try {
            System.out.println("Iniciando respaldo automático de la base de datos...");

            ProcessBuilder pb;
            if (password.isEmpty()) {
                pb = new ProcessBuilder(rutaMysqlDump, "-u", usuario, baseDatos);
            } else {
                pb = new ProcessBuilder(rutaMysqlDump, "-u", usuario, "-p" + password, baseDatos);
            }

            pb.redirectOutput(archivoSalida);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);

            Process proceso = pb.start();
            int resultado = proceso.waitFor();

            if (resultado == 0) {
                System.out.println("¡Respaldo guardado con éxito en: " + archivoSalida.getAbsolutePath());
            } else {
                System.err.println("Hubo un error al ejecutar mysqldump. Código de salida: " + resultado);
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Error crítico durante el proceso de respaldo: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}