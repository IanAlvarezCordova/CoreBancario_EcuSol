//features/bancaweb/WebService.java
package com.ecusol.ecusolcore.features.bancaweb;

import com.ecusol.ecusolcore.core.modelo.*;
import com.ecusol.ecusolcore.core.repositorio.*;

import com.ecusol.ecusolcore.features.bancaweb.dto.CuentaDTO;
import com.ecusol.ecusolcore.features.bancaweb.dto.MovimientoDTO;
import com.ecusol.ecusolcore.features.bancaweb.dto.TransferenciaWebDTO;

import java.util.Random;
import com.ecusol.ecusolcore.features.transacciones.CoreTransaccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import com.ecusol.ecusolcore.features.bancaweb.dto.DestinatarioDTO;
import com.ecusol.ecusolcore.core.repositorio.ClientePersonaRepository;

@Service
public class WebService {

    @Autowired private CuentaRepository cuentaRepo;
    @Autowired private MovimientoRepository movRepo;
    @Autowired private CoreTransaccionService coreService;
    @Autowired private ClientePersonaRepository clienteRepo;

    public List<CuentaDTO> obtenerMisCuentas(Long clienteId) {
        return cuentaRepo.findByClientePersonaId(clienteId).stream()
                .map(c -> new CuentaDTO(c.getCuentaId(), c.getNumeroCuenta(), c.getSaldo(), c.getEstado(), c.getTipoCuentaId()))
                .collect(Collectors.toList());
    }

    public List<MovimientoDTO> obtenerMisMovimientos(String numeroCuenta, Long clienteId) {
        Cuenta cuenta = cuentaRepo.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        // Seguridad: Verificar dueño
        if (!cuenta.getClientePersonaId().equals(clienteId)) {
            throw new RuntimeException("Acceso Denegado: Esta cuenta no te pertenece");
        }

        return movRepo.findByCuentaIdOrderByFechaDesc(cuenta.getCuentaId()).stream()
                .map(m -> new MovimientoDTO(m.getFecha(), m.getTipoMovimiento(), m.getMonto(), m.getSaldoNuevo()))
                .collect(Collectors.toList());
    }

    // === NUEVO MÉTODO PARA BUSCAR DESTINATARIO ===
    public DestinatarioDTO validarDestinatario(String numeroCuenta) {
        // 1. Buscar la cuenta destino
        Cuenta cuenta = cuentaRepo.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        // 2. Obtener datos del dueño
        ClientePersona cliente = clienteRepo.findById(cuenta.getClientePersonaId())
                .orElseThrow(() -> new RuntimeException("Titular no encontrado"));

        // 3. Retornar DTO con datos enmascarados (Seguridad)
        return new DestinatarioDTO(
                cuenta.getNumeroCuenta(),
                cliente.getNombres() + " " + cliente.getApellidos(), // Nombre completo
                maskCedula(cliente.getCedula()) // Cédula oculta
        );
    }
    private String maskCedula(String cedula) {
        if (cedula == null || cedula.length() < 4) return "***";
        return "***" + cedula.substring(cedula.length() - 4);
    }

    @Transactional
    public String solicitarNuevaCuenta(Long clienteId, Long tipoCuentaId) {
        // 1. REGLA DE NEGOCIO: Verificar si ya tiene una solicitud pendiente
        List<Cuenta> misCuentas = cuentaRepo.findByClientePersonaId(clienteId);

        boolean tienePendiente = misCuentas.stream()
                .anyMatch(c -> "INACTIVA".equals(c.getEstado()));

        if (tienePendiente) {
            throw new RuntimeException("Ya tienes una solicitud de cuenta en proceso. Espera a que sea aprobada.");
        }

        // 2. Generar número de cuenta (Prefijo 10 + Random)
        String numeroGenerado = "10" + String.format("%08d", new Random().nextInt(99999999));
        while(cuentaRepo.findByNumeroCuenta(numeroGenerado).isPresent()) {
            numeroGenerado = "10" + String.format("%08d", new Random().nextInt(99999999));
        }

        // 3. Crear la cuenta
        Cuenta nuevaCuenta = new Cuenta();
        nuevaCuenta.setEntidadId(1L); // SIEMPRE ECUSOL
        nuevaCuenta.setTipoCuentaId(tipoCuentaId); // 1=Ahorros, 2=Corriente
        nuevaCuenta.setClientePersonaId(clienteId);
        nuevaCuenta.setNumeroCuenta(numeroGenerado);
        nuevaCuenta.setSaldo(java.math.BigDecimal.ZERO);
        nuevaCuenta.setFechaApertura(java.time.LocalDate.now());
        nuevaCuenta.setEstado("INACTIVA"); // Nace bloqueada

        cuentaRepo.save(nuevaCuenta);

        return nuevaCuenta.getNumeroCuenta();
    }

    @Transactional
    public String transferir(Long clienteId, TransferenciaWebDTO req) {
        Cuenta origen = cuentaRepo.findByNumeroCuenta(req.cuentaOrigen())
                .orElseThrow(() -> new RuntimeException("Cuenta origen incorrecta"));

        // Seguridad
        if (!origen.getClientePersonaId().equals(clienteId)) throw new RuntimeException("No puedes transferir de una cuenta ajena");
        if (origen.getSaldo().compareTo(req.monto()) < 0) throw new RuntimeException("Saldo insuficiente");

        Cuenta destino = cuentaRepo.findByNumeroCuenta(req.cuentaDestino())
                .orElseThrow(() -> new RuntimeException("Cuenta destino no existe"));

        Transaccion t = new Transaccion();
        t.setTipo("TRANSFERENCIA");
        t.setCanal("WEB");
        t.setMonto(req.monto());
        t.setDescripcion(req.descripcion());
        t.setCuentaOrigenId(origen.getCuentaId());
        t.setCuentaDestinoId(destino.getCuentaId());
        t.setReferencia("TRF-WEB-" + System.currentTimeMillis());

        return coreService.procesarTransferencia(t, origen, destino, req.monto());
    }
}