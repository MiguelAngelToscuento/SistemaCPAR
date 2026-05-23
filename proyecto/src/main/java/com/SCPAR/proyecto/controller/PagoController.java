package com.SCPAR.proyecto.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/realizar")
    public String mostrarFormularioBusqueda(Model model) {
        model.addAttribute("cuenta", null);
        return "pago-busqueda";
    }

    @PostMapping("/buscar-cuenta")
    public String buscarCuentaPorFolio(@RequestParam("folio") String folio, Model model) { // Cambiado a String
        CuentaServicio cuenta = cuentaRepository.findById(folio).orElse(null);

        if (cuenta == null) {
            model.addAttribute("error", "El folio " + folio + " no existe en la base de datos.");
            model.addAttribute("cuenta", null);
            return "pago-busqueda";
        }

        CatTipoServicio servicio = catTipoServicioRepository.findById(cuenta.getIdServicio()).orElse(null);
        String nombreServicio = (servicio != null) ? servicio.getNombreServicio() : "Desconocido";
        BigDecimal tarifa = (servicio != null) ? servicio.getTarifa() : BigDecimal.ZERO;

        model.addAttribute("cuenta", cuenta);
        model.addAttribute("tipoServicio", nombreServicio);
        model.addAttribute("tarifaBase", tarifa);
        model.addAttribute("folioBuscado", folio);

        int anioActual = LocalDate.now().getYear();
        List<Integer> listaAnios = new ArrayList<>();
        for (int i = anioActual - 2; i <= anioActual + 5; i++) {
            listaAnios.add(i);
        }
        model.addAttribute("listaAnios", listaAnios);
        model.addAttribute("anioActual", anioActual);
        model.addAttribute("mesActual", LocalDate.now().getMonthValue());

        return "pago-busqueda";
    }

    @PostMapping("/guardar-final")
    @Transactional
    public String guardarCobro(
            @RequestParam("folio") String folio, // Cambiado a String
            @RequestParam("mesInicio") int mesInicio,
            @RequestParam("anioInicio") int anioInicio,
            @RequestParam("mesFin") int mesFin,
            @RequestParam("anioFin") int anioFin,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            CuentaServicio cuenta = cuentaRepository.findById(folio).orElseThrow();
            CatTipoServicio servicio = catTipoServicioRepository.findById(cuenta.getIdServicio()).orElseThrow();
            BigDecimal tarifaBase = servicio.getTarifa();

            int cantidadMeses = ((anioFin - anioInicio) * 12) + (mesFin - mesInicio) + 1;

            if (cantidadMeses <= 0) {
                redirectAttributes.addFlashAttribute("error", "Error en el rango: La fecha final debe ser posterior o igual a la de inicio.");
                return "redirect:/pagos/realizar";
            }

            BigDecimal montoTotal = tarifaBase.multiply(new BigDecimal(cantidadMeses));

            if (cuenta.getDescuentoInapam() != null && cuenta.getDescuentoInapam()) {
                BigDecimal descuentoFijo = new BigDecimal("20").multiply(new BigDecimal(cantidadMeses));
                montoTotal = montoTotal.subtract(descuentoFijo);
            }

            Administrador cajero = administradorRepository.findByCorreo(principal.getName());

            Pago nuevoPago = new Pago();
            nuevoPago.setCuenta(cuenta);
            nuevoPago.setAdministrador(cajero);
            nuevoPago.setMontoTotal(montoTotal);

            Pago pagoGuardado = pagoRepository.save(nuevoPago);

            BigDecimal montoPorMes = montoTotal.divide(new BigDecimal(cantidadMeses), 2, RoundingMode.HALF_UP);
            LocalDate mesBase = LocalDate.of(anioInicio, mesInicio, 1);

            for (int i = 0; i < cantidadMeses; i++) {
                DetallePago detalle = new DetallePago();
                detalle.setPago(pagoGuardado);
                detalle.setPeriodoCubierto(mesBase.plusMonths(i));
                detalle.setMontoAplicado(montoPorMes);

                detallePagoRepository.save(detalle);
            }

            redirectAttributes.addFlashAttribute("mensajeExito", "¡Pago registrado con éxito! Total cobrado: $" + montoTotal);
            return "redirect:/administrador/menu";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar el pago: " + e.getMessage());
            return "redirect:/pagos/realizar";
        }
    }
}