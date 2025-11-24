//src/features/ventanilla/VentanillaService.java
package com.ecusol.ecusolcore.features.ventanilla;

import com.ecusol.ecusolcore.core.modelo.*;
import com.ecusol.ecusolcore.core.repositorio.*;
import com.ecusol.ecusolcore.features.transacciones.CoreTransaccionService;
import com.ecusol.ecusolcore.features.ventanilla.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VentanillaService {

    @Autowired private CuentaRepository cuentaRepo;
    @Autowired private ClientePersonaRepository clienteRepo;
    @Autowired private CoreTransaccionService coreService;
    @Autowired private TransaccionRepository transRepo;
    @Autowired private MovimientoRepository movRepo;

    // 1. BUSCAR CLIENTE GLOBAL
    public ResumenClienteDTO buscarPorCedula(String cedula) {
        ClientePersona cliente = clienteRepo.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("No existe cliente con esa cédula"));

        List<Cuenta> cuentas = cuentaRepo.findByClientePersonaId(cliente.getClienteId());

        List<CuentaResumenDTO> cuentasDTO = cuentas.stream().map(c -> new CuentaResumenDTO(
                c.getNumeroCuenta(),
                c.getTipoCuentaId() == 1L ? "AHORROS" : "CORRIENTE",
                c.getSaldo(),
                c.getEstado()
        )).collect(Collectors.toList());

        return new ResumenClienteDTO(
                cliente.getNombres() + " " + cliente.getApellidos(),
                cliente.getCedula(),
                cliente.getEmail(),
                cliente.getEstado(),
                cuentasDTO
        );
    }

    // 2. CAMBIAR ESTADO CLIENTE (ESTRICTO: ACTIVO / INACTIVO - MASCULINO)
    @Transactional
    public String cambiarEstadoCliente(String cedula, String nuevoEstado) {
        String estadoUpper = nuevoEstado.toUpperCase().trim();

        // VALIDACIÓN ESTRICTA MASCULINA PARA CLIENTES
        if (!"ACTIVO".equals(estadoUpper) && !"INACTIVO".equals(estadoUpper)) {
            throw new RuntimeException("Estado de Cliente inválido. Use ACTIVO o INACTIVO");
        }

        ClientePersona cliente = clienteRepo.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        cliente.setEstado(estadoUpper);
        clienteRepo.save(cliente);

        return "Cliente " + cliente.getNombres() + " ahora está " + estadoUpper;
    }

    // 3. CAMBIAR ESTADO CUENTA (ESTRICTO: ACTIVA / INACTIVA - FEMENINO)
    @Transactional
    public String cambiarEstadoCuenta(String numeroCuenta, String nuevoEstado) {
        String estadoUpper = nuevoEstado.toUpperCase().trim();

        // VALIDACIÓN ESTRICTA FEMENINA PARA CUENTAS
        if (!"ACTIVA".equals(estadoUpper) && !"INACTIVA".equals(estadoUpper)) {
            throw new RuntimeException("Estado de Cuenta inválido. Use ACTIVA o INACTIVA");
        }

        Cuenta cuenta = cuentaRepo.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        cuenta.setEstado(estadoUpper);
        cuentaRepo.save(cuenta);

        return "Estado de cuenta actualizado a: " + estadoUpper;
    }

    // 4. ACTIVAR CUENTA (Rápido) -> Pone ACTIVA
    @Transactional
    public String activarCuenta(String numeroCuenta) {
        Cuenta cuenta = cuentaRepo.findByNumeroCuenta(numeroCuenta).orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        if ("ACTIVA".equals(cuenta.getEstado().toUpperCase())) {
            throw new RuntimeException("La cuenta ya está activa");
        }

        cuenta.setEstado("ACTIVA"); // Femenino
        cuentaRepo.save(cuenta);
        return "Cuenta activada correctamente.";
    }

    // 5. VALIDACIONES Y BÚSQUEDAS SIMPLES
    public InfoCuentaDTO validarDestinatario(String numeroCuenta) {
        Cuenta cuenta = cuentaRepo.findByNumeroCuenta(numeroCuenta).orElseThrow(() -> new RuntimeException("Cuenta no existe"));
        ClientePersona cliente = clienteRepo.findById(cuenta.getClientePersonaId()).orElseThrow(() -> new RuntimeException("Titular no encontrado"));
        String tipo = cuenta.getTipoCuentaId() == 1L ? "AHORROS" : "CORRIENTE";
        return new InfoCuentaDTO(cuenta.getNumeroCuenta(), cliente.getNombres() + " " + cliente.getApellidos(), null, tipo);
    }

    public InfoCuentaDTO obtenerInfoCuenta(String numeroCuenta) {
        Cuenta cuenta = cuentaRepo.findByNumeroCuenta(numeroCuenta).orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
        ClientePersona cliente = clienteRepo.findById(cuenta.getClientePersonaId()).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        String tipo = cuenta.getTipoCuentaId() == 1L ? "AHORROS" : "CORRIENTE";
        return new InfoCuentaDTO(cuenta.getNumeroCuenta(), cliente.getNombres() + " " + cliente.getApellidos(), cuenta.getSaldo(), tipo);
    }

    // 6. OPERAR
    @Transactional
    public String procesarOperacion(String tipo, VentanillaOpDTO req, Long sucursalId) {
        Cuenta cuentaPrincipal = cuentaRepo.findByNumeroCuenta(req.numeroCuentaOrigen())
                .orElseThrow(() -> new RuntimeException("Cuenta cliente no encontrada"));

        // Validar cuenta origen: ACTIVA
        String estadoPrincipal = cuentaPrincipal.getEstado().toUpperCase();
        if (!"ACTIVA".equals(estadoPrincipal)) {
            throw new RuntimeException("La Cuenta está INACTIVA/BLOQUEADA");
        }

        Transaccion t = new Transaccion();
        t.setTipo(tipo);
        t.setCanal("CAJERO");
        t.setMonto(req.monto());
        t.setDescripcion(req.descripcion() != null ? req.descripcion() : tipo);
        t.setSucursalId(sucursalId);
        t.setReferencia("VEN-" + System.currentTimeMillis());

        if ("TRANSFERENCIA".equals(tipo)) {
            if (cuentaPrincipal.getSaldo().compareTo(req.monto()) < 0) throw new RuntimeException("Fondos insuficientes");

            Cuenta destino = cuentaRepo.findByNumeroCuenta(req.numeroCuentaDestino())
                    .orElseThrow(() -> new RuntimeException("Cuenta destino no existe"));

            // Validar cuenta destino: ACTIVA
            String estadoDestino = destino.getEstado().toUpperCase();
            if (!"ACTIVA".equals(estadoDestino)) {
                throw new RuntimeException("Cuenta destino inactiva");
            }

            t.setCuentaOrigenId(cuentaPrincipal.getCuentaId());
            t.setCuentaDestinoId(destino.getCuentaId());

            return coreService.procesarTransferencia(t, cuentaPrincipal, destino, req.monto());

        } else if ("DEPOSITO".equals(tipo)) {
            t.setCuentaDestinoId(cuentaPrincipal.getCuentaId());
            return coreService.procesarTransaccionBase(t, cuentaPrincipal, "C", req.monto());

        } else { // RETIRO
            if (cuentaPrincipal.getSaldo().compareTo(req.monto()) < 0) throw new RuntimeException("Fondos insuficientes");
            t.setCuentaOrigenId(cuentaPrincipal.getCuentaId());
            return coreService.procesarTransaccionBase(t, cuentaPrincipal, "D", req.monto());
        }
    }

    @Transactional
    public String eliminarCuenta(String numeroCuenta) {
        Cuenta cuenta = cuentaRepo.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        Long idCuenta = cuenta.getCuentaId();

        movRepo.deleteByCuentaId(idCuenta);

        List<Transaccion> txOrigen = transRepo.findByCuentaOrigenId(idCuenta);
        List<Transaccion> txDestino = transRepo.findByCuentaDestinoId(idCuenta);

        for (Transaccion t : txOrigen) movRepo.deleteByTransaccionId(t.getTransaccion_id());
        for (Transaccion t : txDestino) movRepo.deleteByTransaccionId(t.getTransaccion_id());

        transRepo.deleteAll(txOrigen);
        transRepo.deleteAll(txDestino);

        cuentaRepo.delete(cuenta);

        return "Cuenta " + numeroCuenta + " eliminada definitivamente.";
    }
}