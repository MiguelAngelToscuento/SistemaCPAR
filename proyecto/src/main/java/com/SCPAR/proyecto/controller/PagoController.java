package com.SCPAR.proyecto.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public String buscarCuentaPorFolio(@RequestParam("folio") String folio, Model model) {
        CuentaServicio cuenta = cuentaRepository.findById(folio).orElse(null);

        if (cuenta == null) {
            model.addAttribute("error", "El folio " + folio + " no existe en la base de datos.");
            model.addAttribute("cuenta", null);
            return "pago-busqueda";
        }

        // --- NUEVO CANDADO: Evitar pagos si está suspendida ---
        if (cuenta.getEstatusCuenta() != null && cuenta.getEstatusCuenta() == 0) {
            model.addAttribute("error", "La cuenta con folio " + folio + " está SUSPENDIDA. No se pueden procesar cobros.");
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

            // --- CANDADO BACKEND ANTES DE COBRAR ---
            if (cuenta.getEstatusCuenta() != null && cuenta.getEstatusCuenta() == 0) {
                redirectAttributes.addFlashAttribute("error", "Operación denegada: La cuenta está suspendida.");
                return "redirect:/pagos/realizar";
            }

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

            redirectAttributes.addFlashAttribute("mensajeExito", "¡Pago registrado con éxito!");
            // Redirigimos al recibo usando el ID del pago que acabamos de guardar en la BD
            return "redirect:/pagos/recibo/" + pagoGuardado.getIdPago();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar el pago: " + e.getMessage());
            return "redirect:/pagos/realizar";
        }
    }

    // 4. MOSTRAR EL RECIBO DE PAGO
    @GetMapping("/recibo/{idPago}")
    public String mostrarRecibo(@PathVariable Integer idPago, Model model, RedirectAttributes redirectAttributes) {
        // Buscamos el ticket en la base de datos
        Pago pago = pagoRepository.findById(idPago).orElse(null);

        if (pago == null) {
            redirectAttributes.addFlashAttribute("error", "El recibo solicitado no existe.");
            return "redirect:/administrador/menu";
        }

        // Buscar el servicio usando el idServicio de la cuenta y extraer la tarifa
        CatTipoServicio servicio = catTipoServicioRepository.findById(pago.getCuenta().getIdServicio()).orElse(null);
        BigDecimal tarifaServicio = (servicio != null) ? servicio.getTarifa() : BigDecimal.ZERO;

        // Agregar la tarifa al modelo para usarla en la vista
        model.addAttribute("pago", pago);
        model.addAttribute("tarifaServicio", tarifaServicio);

        return "recibo-pago";
    }

    @GetMapping("/adeudos/lista")
    public String verListaDeudores(Model model) {
        // Jalamos la lista de la base de datos usando el repositorio
        List<Map<String, Object>> deudores = pagoRepository.findCuentasConAdeudo();

        // Se la pasamos a la nueva vista de Thymeleaf
        model.addAttribute("listaDeudores", deudores);

        // Buscamos el archivo llamado lista_adeudos.html en la carpeta templates
        return "lista_adeudos";
    }
}
