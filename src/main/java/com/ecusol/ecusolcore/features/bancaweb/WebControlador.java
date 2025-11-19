package com.ecusol.ecusolcore.features.bancaweb;


// SOLO importamos los DTOs específicos de este módulo
import com.ecusol.ecusolcore.features.bancaweb.dto.CuentaDTO;
import com.ecusol.ecusolcore.features.bancaweb.dto.MovimientoDTO;
import com.ecusol.ecusolcore.features.bancaweb.dto.TransferenciaWebDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/web")
@CrossOrigin(origins = "*")
@Tag(name = "2. Banca Web", description = "Operaciones del Cliente Logueado")
public class WebControlador {

    @Autowired private WebService webService;

    @GetMapping("/cuentas")
    @Operation(summary = "Ver mis cuentas y saldos")
    public List<CuentaDTO> misCuentas(@AuthenticationPrincipal Long clienteId) {
        return webService.obtenerMisCuentas(clienteId);
    }

    @GetMapping("/movimientos/{numeroCuenta}")
    @Operation(summary = "Ver movimientos de una cuenta específica")
    public List<MovimientoDTO> movimientos(@PathVariable String numeroCuenta, @AuthenticationPrincipal Long clienteId) {
        return webService.obtenerMisMovimientos(numeroCuenta, clienteId);
    }

    @PostMapping("/transferir")
    @Operation(summary = "Transferencia interna entre cuentas")
    public String transferir(@AuthenticationPrincipal Long clienteId, @RequestBody TransferenciaWebDTO req) {
        return webService.transferir(clienteId, req);
    }

    @PostMapping("/solicitar-cuenta")
    @Operation(summary = "El usuario solicita abrir una nueva cuenta (Queda INACTIVA)")
    public String solicitarCuenta(@AuthenticationPrincipal Long clienteId, @RequestParam Long tipoCuentaId) {
        return webService.solicitarNuevaCuenta(clienteId, tipoCuentaId);
    }

    @GetMapping("/validar-destinatario/{numeroCuenta}")
    @Operation(summary = "Obtener nombre del titular de una cuenta destino")
    public com.ecusol.ecusolcore.features.bancaweb.dto.DestinatarioDTO validarDestinatario(@PathVariable String numeroCuenta) {
        return webService.validarDestinatario(numeroCuenta);
    }
}