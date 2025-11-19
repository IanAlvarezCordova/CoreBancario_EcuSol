package com.ecusol.ecusolcore.features.ventanilla;

import com.ecusol.ecusolcore.features.ventanilla.dto.VentanillaOpDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//permitir CORS PARA TODOS

@RestController
@RequestMapping("/api/ventanilla")
@CrossOrigin(origins = "*")

@Tag(name = "1. Módulo Ventanilla", description = "Operaciones de Caja (Depósitos/Retiros)")
public class VentanillaControlador {

    @Autowired private VentanillaService service;

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
}