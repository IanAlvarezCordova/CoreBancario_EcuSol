package com.ecusol.ecusolcore.features.shared;

import com.ecusol.ecusolcore.core.modelo.*;
import com.ecusol.ecusolcore.core.repositorio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
public class CoreTransaccionService {

    @Autowired private TransaccionRepository transRepo;
    @Autowired private MovimientoRepository movRepo;

    // Método genérico para registrar cualquier operación contable
    @Transactional
    public String procesarTransaccionBase(Transaccion transaccion, Cuenta cuentaPrincipal, String tipoMovimiento, BigDecimal monto) {
        // 1. Guardar Transacción
        transRepo.save(transaccion);

        // 2. Guardar Movimiento
        registrarMovimiento(transaccion, cuentaPrincipal, tipoMovimiento, monto);

        return transaccion.getReferencia();
    }

    // Método específico para transferencias (Dos movimientos)
    @Transactional
    public String procesarTransferencia(Transaccion transaccion, Cuenta origen, Cuenta destino, BigDecimal monto) {
        // 1. VALIDACIÓN DE ESTADO (NUEVO)
        if (!"ACTIVA".equals(origen.getEstado())) {
            throw new RuntimeException("La cuenta de origen no está activa.");
        }
        if (!"ACTIVA".equals(destino.getEstado())) {
            throw new RuntimeException("La cuenta de destino no está activa.");
        }

        // 2. Guardar todo
        transRepo.save(transaccion);
        registrarMovimiento(transaccion, origen, "D", monto);
        registrarMovimiento(transaccion, destino, "C", monto);
        return transaccion.getReferencia();
    }

    private void registrarMovimiento(Transaccion t, Cuenta c, String tipo, BigDecimal monto) {
        BigDecimal saldoAnterior = c.getSaldo();
        BigDecimal saldoNuevo = tipo.equals("C") ? saldoAnterior.add(monto) : saldoAnterior.subtract(monto);

        Movimiento m = new Movimiento();
        m.setTransaccionId(t.getTransaccion_id());
        m.setCuentaId(c.getCuentaId());
        m.setTipoMovimiento(tipo);
        m.setMonto(monto);
        m.setSaldoAnterior(saldoAnterior);
        m.setSaldoNuevo(saldoNuevo);

        movRepo.save(m);
        // NOTA: El trigger de la BD actualizará el saldo real de la cuenta,
        // pero guardamos el histórico aquí por auditoría.
    }
}