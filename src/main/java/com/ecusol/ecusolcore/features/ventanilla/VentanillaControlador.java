package com.ecusol.ecusolcore.features.ventanilla;

import com.ecusol.ecusolcore.features.ventanilla.dto.InfoCuentaDTO;
import com.ecusol.ecusolcore.features.ventanilla.dto.VentanillaOpDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ventanilla")
@Tag(name = "1. Módulo Ventanilla", description = "Operaciones de Caja (Depósitos/Retiros/Transferencias)")
public class VentanillaControlador {

    @Autowired private VentanillaService service;

    @GetMapping("/info/{numeroCuenta}")
    @Operation(summary = "Obtener saldo y datos básicos de una cuenta")
    public InfoCuentaDTO obtenerInfo(@PathVariable String numeroCuenta) {
        return service.obtenerInfoCuenta(numeroCuenta);
    }

    @PostMapping("/deposito")
    @Operation(summary = "Realizar Depósito en Efectivo")
    public String deposito(@RequestBody VentanillaOpDTO req, @RequestParam Long sucursalId) {
        return service.deposito(req, sucursalId);
    }

    @PostMapping("/retiro")
    @Operation(summary = "Realizar Retiro en Efectivo")
    public String retiro(@RequestBody VentanillaOpDTO req, @RequestParam Long sucursalId) {
        return service.retiro(req, sucursalId);
    }

    @PostMapping("/transferencia")
    @Operation(summary = "Realizar Transferencia desde Ventanilla")
    public String transferencia(@RequestBody VentanillaOpDTO req, @RequestParam Long sucursalId) {
        return service.realizarTransferencia(req, sucursalId);
    }
}