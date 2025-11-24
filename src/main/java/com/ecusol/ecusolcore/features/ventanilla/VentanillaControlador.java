//features/ventanilla/VentanillaControlador.java
package com.ecusol.ecusolcore.features.ventanilla;

import com.ecusol.ecusolcore.features.ventanilla.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ventanilla")
@Tag(name = "1. Módulo Ventanilla (Empleados)", description = "Terminal de Caja")
public class VentanillaControlador {

    @Autowired private VentanillaService service;

    // En el futuro, esto vendría del token del empleado. Por ahora hardcodeado.
    private final Long SUCURSAL_DEFAULT = 1L;

    @GetMapping("/buscar-cliente/{cedula}")
    @Operation(summary = "Buscar todos los productos de un cliente por Cédula")
    public ResumenClienteDTO buscarPorCedula(@PathVariable String cedula) {
        return service.buscarPorCedula(cedula);
    }

    @GetMapping("/validar-destino/{cuenta}")
    @Operation(summary = "Validar cuenta destino para transferencia")
    public InfoCuentaDTO validarDestino(@PathVariable String cuenta) {
        return service.validarDestinatario(cuenta);
    }

    @GetMapping("/info/{numeroCuenta}")
    public InfoCuentaDTO obtenerInfo(@PathVariable String numeroCuenta) {
        // CORREGIDO: El método en el servicio es 'obtenerInfoCuenta'
        return service.obtenerInfoCuenta(numeroCuenta);
    }

    @PostMapping("/operar/{tipo}")
    public String operar(@PathVariable String tipo, @RequestBody VentanillaOpDTO req) {
        return service.procesarOperacion(tipo.toUpperCase(), req, SUCURSAL_DEFAULT);
    }

    @PostMapping("/activar-cuenta/{cuenta}")
    @Operation(summary = "Activar una cuenta que estaba pendiente (INACTIVA)")
    public String activarCuenta(@PathVariable String cuenta) {
        return service.activarCuenta(cuenta);
    }

    @PostMapping("/cuenta/estado")
    public String cambiarEstadoCuenta(@RequestParam String cuenta, @RequestParam String estado) {
        return service.cambiarEstadoCuenta(cuenta, estado);
    }

    @PostMapping("/cliente/estado")
    public String cambiarEstadoCliente(@RequestParam String cedula, @RequestParam String estado) {
        return service.cambiarEstadoCliente(cedula, estado);
    }

    @DeleteMapping("/cuenta/{numeroCuenta}")
    @Operation(summary = "Eliminar permanentemente una cuenta y su historial")
    public String eliminarCuenta(@PathVariable String numeroCuenta) {
        return service.eliminarCuenta(numeroCuenta);
    }
}