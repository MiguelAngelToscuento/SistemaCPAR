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

    @Value("${backups.ruta-carpeta:}")
    private String rutaCarpeta;

    @Value("${backups.comando-dump:}")
    private String rutaMysqlDump;

    // --- CREDENCIALES DE TU BASE DE DATOS ---
    private final String usuario = "root";
    private final String password = "7585ToManf";
    private final String baseDatos = "sistema_agua_texcalac";

    // 1. EL RESPALDO AUTOMÁTICO (Cada quincena a las 2 AM)
    @Scheduled(cron = "0 0 2 */15 * *")
    public void crearRespaldoAutomatico() {
        ejecutarProcesoRespaldo("respaldo_sistema_agua_");
    }

    // 2. EL RESPALDO MANUAL (El que llama el botón de tu Menú)
    public boolean ejecutarRespaldoManual() {
        return ejecutarProcesoRespaldo("respaldo_MANUAL_");
    }

    // 3. EL MOTOR PRINCIPAL (Hace el trabajo para ambos)
    private boolean ejecutarProcesoRespaldo(String prefijoArchivo) {

        // Detección automática del sistema operativo para las rutas
        String os = System.getProperty("os.name").toLowerCase();
        String rutaCarpetaFinal = rutaCarpeta;
        String rutaDumpFinal = rutaMysqlDump;

        // Si las propiedades están vacías, usamos las rutas por defecto según el SO
        if (rutaCarpetaFinal == null || rutaCarpetaFinal.isEmpty()) {
            if (os.contains("win")) {
                rutaCarpetaFinal = "C:/Respaldos_Texcalac/";
                rutaDumpFinal = "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump.exe";
            } else {
                rutaCarpetaFinal = "/opt/respaldos_texcalac/";
                rutaDumpFinal = "mysqldump"; // Comando global en CachyOS/Linux
            }
        }

        File carpeta = new File(rutaCarpetaFinal);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String nombreArchivo = prefijoArchivo + fechaActual + ".sql";
        File archivoSalida = new File(carpeta, nombreArchivo);

        try {
            System.out.println("Iniciando respaldo de la base de datos...");

            ProcessBuilder pb;
            if (password.isEmpty()) {
                pb = new ProcessBuilder(rutaDumpFinal, "-u", usuario, baseDatos);
            } else {
                // El parámetro -p va pegado a la contraseña en mysqldump
                pb = new ProcessBuilder(rutaDumpFinal, "-u", usuario, "-p" + password, baseDatos);
            }

            pb.redirectOutput(archivoSalida);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);

            Process proceso = pb.start();
            int resultado = proceso.waitFor();

            if (resultado == 0) {
                System.out.println("¡Respaldo guardado con éxito en: " + archivoSalida.getAbsolutePath());
                return true;
            } else {
                System.err.println("Error al ejecutar mysqldump. Código de salida: " + resultado);
                return false;
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Error crítico durante el proceso de respaldo: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }
}