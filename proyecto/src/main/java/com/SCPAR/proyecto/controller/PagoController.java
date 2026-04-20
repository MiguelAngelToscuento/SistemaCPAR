package com.SCPAR.proyecto.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.SCPAR.proyecto.model.Administrador;
import com.SCPAR.proyecto.model.CatTipoServicio;
import com.SCPAR.proyecto.model.CuentaServicio;
import com.SCPAR.proyecto.model.DetallePago;
import com.SCPAR.proyecto.model.Pago;
import com.SCPAR.proyecto.repository.AdministradorRepository;
import com.SCPAR.proyecto.repository.CatTipoServicioRepository;
import com.SCPAR.proyecto.repository.CuentaServicioRepository;
import com.SCPAR.proyecto.repository.DetallePagoRepository;
import com.SCPAR.proyecto.repository.PagoRepository;

import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/pagos")
public class PagoController {

    @Autowired
    private CuentaServicioRepository cuentaRepository;
    @Autowired
    private PagoRepository pagoRepository;
    @Autowired
    private CatTipoServicioRepository catTipoServicioRepository;
    @Autowired
    private AdministradorRepository administradorRepository;
    @Autowired
    private DetallePagoRepository detallePagoRepository;

    // 1. MOSTRAR PANTALLA DE COBRO
    @GetMapping("/realizar")
    public String mostrarFormularioBusqueda() {
        return "pago-busqueda";
    }

    // 2. API PARA BUSCAR DATOS AL VUELO (Lo que hicimos en la Fase 2)
    @GetMapping("/api/buscar-cuenta")
    @ResponseBody
    public ResponseEntity<?> buscarCuentaPorFolio(@RequestParam("folio") Integer folio) {
        CuentaServicio cuenta = cuentaRepository.findById(folio).orElse(null);
        if (cuenta == null) return ResponseEntity.notFound().build();

        CatTipoServicio servicio = catTipoServicioRepository.findById(cuenta.getIdServicio()).orElse(null);
        BigDecimal tarifa = (servicio != null) ? servicio.getTarifa() : BigDecimal.ZERO;
        String nombreServicio = (servicio != null) ? servicio.getNombreServicio() : "Desconocido";

        Map<String, Object> respuesta = new HashMap<>();
        String nombreCompleto = cuenta.getNombres() + " " + cuenta.getApellidoPaterno();
        if (cuenta.getApellidoMaterno() != null) nombreCompleto += " " + cuenta.getApellidoMaterno();

        respuesta.put("nombreCompleto", nombreCompleto);
        respuesta.put("tipoServicio", nombreServicio);
        respuesta.put("tarifaBase", tarifa);
        respuesta.put("tieneInapam", cuenta.getDescuentoInapam());

        return ResponseEntity.ok(respuesta);
    }

    // 3. GUARDAR EL PAGO FINAL EN LA BASE DE DATOS
    @PostMapping("/guardar-final")
    @Transactional // Esto asegura que si algo falla, no se guarde a medias
    public String guardarCobro(
            @RequestParam("folio") Integer folio,
            @RequestParam("cantidadMeses") Integer cantidadMeses,
            @RequestParam("monto") BigDecimal montoTotal,
            Principal principal, // Para saber quién inició sesión
            RedirectAttributes redirectAttributes) {

        try {
            // A. Buscar la cuenta que va a pagar
            CuentaServicio cuenta = cuentaRepository.findById(folio).orElseThrow();

            // B. Buscar al administrador que está cobrando (usando el correo del login)
            Administrador cajero = administradorRepository.findByCorreo(principal.getName());

            // C. Crear el Ticket Principal (Tabla `pagos`)
            Pago nuevoPago = new Pago();
            nuevoPago.setCuenta(cuenta);
            nuevoPago.setAdministrador(cajero);
            nuevoPago.setMontoTotal(montoTotal);

            // Guardamos el ticket principal
            Pago pagoGuardado = pagoRepository.save(nuevoPago);

            // D. Crear el desglose de meses (Tabla `detalle_pago`)
            // Dividimos el total entre los meses para saber cuánto se pagó por cada mes
            BigDecimal montoPorMes = montoTotal.divide(new BigDecimal(cantidadMeses), 2, RoundingMode.HALF_UP);
            LocalDate mesBase = LocalDate.now(); // Empezamos a contar desde el mes actual

            for (int i = 0; i < cantidadMeses; i++) {
                DetallePago detalle = new DetallePago();
                detalle.setPago(pagoGuardado); // Lo enlazamos al ticket principal
                detalle.setPeriodoCubierto(mesBase.plusMonths(i)); // Genera Enero, luego Febrero, etc.
                detalle.setMontoAplicado(montoPorMes);

                detallePagoRepository.save(detalle); // Guardamos cada mes
            }

            // Si todo sale bien, mandamos mensaje verde
            redirectAttributes.addFlashAttribute("mensajeExito", "¡El pago de $" + montoTotal + " se registró con éxito!");
            return "redirect:/administrador/menu?login=exito";

        } catch (Exception e) {
            // Si algo falla, mandamos mensaje rojo
            redirectAttributes.addFlashAttribute("error", "Error al procesar el pago: " + e.getMessage());
            return "redirect:/pagos/nuevo";
        }
    }
}