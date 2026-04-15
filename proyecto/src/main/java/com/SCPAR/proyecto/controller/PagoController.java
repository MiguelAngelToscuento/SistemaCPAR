package com.SCPAR.proyecto.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; // ¡Importante!

import com.SCPAR.proyecto.model.CuentaServicio;
import com.SCPAR.proyecto.model.Pago;
import com.SCPAR.proyecto.repository.CuentaServicioRepository;
import com.SCPAR.proyecto.repository.PagoRepository;

@Controller
@RequestMapping("/pagos")
public class PagoController {

    @Autowired
    private CuentaServicioRepository cuentaRepository;

    @Autowired
    private PagoRepository pagoRepository;

    // 1. ESTO ES LO QUE TE FALTA: Abre el formulario de búsqueda
    @GetMapping("/realizar")
    public String mostrarFormularioBusqueda() {
        return "pago-busqueda";
    }

    // 2. Procesa los datos del formulario y muestra la confirmación
    @PostMapping("/confirmacion")
    public String procesarPago(
            @RequestParam("folio") Integer folio,
            @RequestParam("monto") BigDecimal monto,
            @RequestParam("mesInicio") String mesInicio,
            @RequestParam("anioInicio") String anioInicio,
            @RequestParam("mesFin") String mesFin,
            @RequestParam("anioFin") String anioFin,
            Model model) {

        // Buscar la cuenta en la base de datos
        CuentaServicio cuenta = cuentaRepository.findById(folio).orElse(null);

        if (cuenta == null) {
            // Si el folio no existe, regresamos al menú con un aviso
            return "redirect:/administrador/menu?error=FolioNoEncontrado";
        }

        // Crear el objeto temporal de Pago
        Pago nuevoPago = new Pago();
        nuevoPago.setCuenta(cuenta);
        nuevoPago.setMontoTotal(monto);

        // Unir el periodo en una sola cadena para la vista
        String periodoCompleto = mesInicio + " " + anioInicio + " - " + mesFin + " " + anioFin;
        nuevoPago.setPeriodo(periodoCompleto);

        // Pasar datos a informacion-pago.html
        model.addAttribute("pago", nuevoPago);
        model.addAttribute("cuenta", cuenta);

        return "informacion-pago";
    }

    // 3. Guarda definitivamente el pago cuando dan clic en "Generar Recibo"
    @PostMapping("/guardar")
    public String guardarPagoFinal(
            @RequestParam("folio") Integer folio,
            @RequestParam("monto") BigDecimal monto,
            @RequestParam("periodo") String periodo) {

        CuentaServicio cuenta = cuentaRepository.findById(folio).orElseThrow();

        Pago pagoFinal = new Pago();
        pagoFinal.setCuenta(cuenta);
        pagoFinal.setMontoTotal(monto);
        pagoFinal.setPeriodo(periodo);
        // La fecha se genera automáticamente en el modelo

        pagoRepository.save(pagoFinal); // Aquí se usa el repositorio y quita el warning

        return "redirect:/administrador/menu?mensajeExito=¡Pago registrado con éxito!";
    }
}
