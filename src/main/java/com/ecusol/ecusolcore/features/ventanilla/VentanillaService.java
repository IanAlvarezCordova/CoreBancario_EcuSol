package com.ecusol.ecusolcore.features.ventanilla;

import com.ecusol.ecusolcore.core.modelo.*;
import com.ecusol.ecusolcore.core.repositorio.*;
import com.ecusol.ecusolcore.features.shared.CoreTransaccionService;
import com.ecusol.ecusolcore.features.ventanilla.dto.InfoCuentaDTO; // Importante
import com.ecusol.ecusolcore.features.ventanilla.dto.VentanillaOpDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VentanillaService {

    @Autowired private CuentaRepository cuentaRepo;
    @Autowired private ClientePersonaRepository clienteRepo; // Necesario para buscar nombres
    @Autowired private CoreTransaccionService coreService;

    // === NUEVO: VER SALDO Y DATOS ===
    public InfoCuentaDTO obtenerInfoCuenta(String numeroCuenta) {
        Cuenta cuenta = cuentaRepo.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        ClientePersona cliente = clienteRepo.findById(cuenta.getClientePersonaId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        String tipo = cuenta.getTipoCuentaId() == 1L ? "AHORROS" : "CORRIENTE";

        return new InfoCuentaDTO(
                cuenta.getNumeroCuenta(),
                cliente.getNombres() + " " + cliente.getApellidos(),
                cuenta.getSaldo(),
                tipo
        );
    }

    // === OPERACIONES ===

    @Transactional
    public String deposito(VentanillaOpDTO req, Long sucursalId) {
        Cuenta cuenta = cuentaRepo.findByNumeroCuenta(req.numeroCuenta())
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        Transaccion t = crearTransaccion("DEPOSITO", req.monto(), req.descripcion(), sucursalId);
        t.setCuentaDestinoId(cuenta.getCuentaId());
        t.setReferencia("DEP-VEN-" + System.currentTimeMillis());

        return coreService.procesarTransaccionBase(t, cuenta, "C", req.monto());
    }

    @Transactional
    public String retiro(VentanillaOpDTO req, Long sucursalId) {
        Cuenta cuenta = cuentaRepo.findByNumeroCuenta(req.numeroCuenta())
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        if (cuenta.getSaldo().compareTo(req.monto()) < 0) {
            throw new RuntimeException("Saldo insuficiente");
        }

        Transaccion t = crearTransaccion("RETIRO", req.monto(), req.descripcion(), sucursalId);
        t.setCuentaOrigenId(cuenta.getCuentaId());
        t.setReferencia("RET-VEN-" + System.currentTimeMillis());

        return coreService.procesarTransaccionBase(t, cuenta, "D", req.monto());
    }

    @Transactional
    public String realizarTransferencia(VentanillaOpDTO req, Long sucursalId) {
        // 1. Validar Origen
        Cuenta origen = cuentaRepo.findByNumeroCuenta(req.numeroCuenta())
                .orElseThrow(() -> new RuntimeException("Cuenta origen no encontrada"));

        if (origen.getSaldo().compareTo(req.monto()) < 0) {
            throw new RuntimeException("Saldo insuficiente");
        }


        Cuenta destino = cuentaRepo.findByNumeroCuenta(req.cuentaDestino())
                .orElseThrow(() -> new RuntimeException("Cuenta destino no encontrada"));


        Transaccion t = new Transaccion();
        t.setTipo("TRANSFERENCIA");


        t.setCanal("CAJERO");


        t.setMonto(req.monto());
        t.setDescripcion(req.descripcion() != null ? req.descripcion() : "Transferencia en Ventanilla");
        t.setSucursalId(sucursalId);
        t.setCuentaOrigenId(origen.getCuentaId());
        t.setCuentaDestinoId(destino.getCuentaId());


        t.setReferencia("TRF-VEN-" + System.currentTimeMillis());

        return coreService.procesarTransferencia(t, origen, destino, req.monto());
    }

    private Transaccion crearTransaccion(String tipo, java.math.BigDecimal monto, String desc, Long sucursalId) {
        Transaccion t = new Transaccion();
        t.setTipo(tipo);
        t.setCanal("CAJERO"); // Usamos CAJERO para cumplir con la BD
        t.setMonto(monto);
        t.setDescripcion(desc);
        t.setSucursalId(sucursalId);
        return t;
    }
}