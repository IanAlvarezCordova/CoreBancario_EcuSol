package com.ecusol.ecusolcore.features.ventanilla;

import com.ecusol.ecusolcore.core.modelo.*;
import com.ecusol.ecusolcore.core.repositorio.*;
import com.ecusol.ecusolcore.features.shared.CoreTransaccionService;
import com.ecusol.ecusolcore.features.ventanilla.dto.VentanillaOpDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class VentanillaService {

    @Autowired private CuentaRepository cuentaRepo;
    @Autowired private CoreTransaccionService coreService;

    @Transactional
    public String deposito(VentanillaOpDTO req, Long sucursalId) {
        Cuenta cuenta = cuentaRepo.findByNumeroCuenta(req.numeroCuenta())
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        Transaccion t = crearTransaccion("DEPOSITO", req.monto(), req.descripcion(), sucursalId);
        t.setCuentaDestinoId(cuenta.getCuentaId());
        t.setReferencia("DEP-CAJ-" + System.currentTimeMillis());

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
        t.setReferencia("RET-CAJ-" + System.currentTimeMillis());

        return coreService.procesarTransaccionBase(t, cuenta, "D", req.monto());
    }

    private Transaccion crearTransaccion(String tipo, BigDecimal monto, String desc, Long sucursalId) {
        Transaccion t = new Transaccion();
        t.setTipo(tipo);
        t.setCanal("CAJERO");
        t.setMonto(monto);
        t.setDescripcion(desc);
        t.setSucursalId(sucursalId);
        return t;
    }
}