// servicio/AuthService.java
package com.ecusol.ecusolcore.features.auth;

import com.ecusol.ecusolcore.config.JwtTokenProvider;
import com.ecusol.ecusolcore.core.modelo.ClientePersona;
import com.ecusol.ecusolcore.core.repositorio.ClientePersonaRepository;
import com.ecusol.ecusolcore.features.administracion.Empleado;
import com.ecusol.ecusolcore.core.repositorio.EmpleadoRepository;
import com.ecusol.ecusolcore.core.modelo.Sucursal;
import com.ecusol.ecusolcore.core.repositorio.SucursalRepository;
import com.ecusol.ecusolcore.features.auth.dto.JwtResponse;
import com.ecusol.ecusolcore.features.auth.dto.LoginRequest;
import com.ecusol.ecusolcore.features.auth.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired private ClientePersonaRepository clienteRepo;
    @Autowired private EmpleadoRepository empleadoRepo;
    @Autowired private SucursalRepository sucursalRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtTokenProvider jwtTokenProvider;

    // REGISTRO
    public String register(RegisterRequest req) {
        if (clienteRepo.existsByUsuario(req.usuario())) throw new RuntimeException("Usuario ya existe");
        if (clienteRepo.existsByEmail(req.email())) throw new RuntimeException("Email ya registrado");

        ClientePersona cliente = ClientePersona.builder()
                .cedula(req.cedula())
                .nombres(req.nombres())
                .apellidos(req.apellidos())
                .email(req.email())
                .telefono(req.telefono())
                .direccion(req.direccion())
                .usuario(req.usuario())
                .passwordHash(passwordEncoder.encode(req.password()))
                .entidadId(1L)
                .estado("ACTIVO") // Nace activo
                .build();

        clienteRepo.save(cliente);
        return "Cliente registrado exitosamente";
    }

    // LOGIN CLIENTES (WEB) - CON VALIDACIÓN DE ESTADO
    public String loginCliente(LoginRequest req) {
        ClientePersona cliente = clienteRepo.findByUsuario(req.usuario())
                .orElseThrow(() -> new RuntimeException("Credenciales incorrectas"));

        if (!passwordEncoder.matches(req.password(), cliente.getPasswordHash())) {
            throw new RuntimeException("Credenciales incorrectas");
        }

        // NUEVO: VALIDACIÓN DE ESTADO
        if (!"ACTIVO".equals(cliente.getEstado())) {
            throw new RuntimeException("Su usuario está INACTIVO. Por favor acérquese a una agencia.");
        }

        cliente.setUltimoLogin(LocalDateTime.now());
        clienteRepo.save(cliente);

        return jwtTokenProvider.createToken(cliente.getUsuario(), cliente.getClienteId(), "CLIENTE");
    }

    // LOGIN EMPLEADOS (VENTANILLA)
    public JwtResponse loginEmpleado(LoginRequest req) {
        Empleado empleado = empleadoRepo.findByUsuario(req.usuario())
                .orElseThrow(() -> new RuntimeException("Empleado no autorizado"));

        if (!passwordEncoder.matches(req.password(), empleado.getPasswordHash())) {
            throw new RuntimeException("Credenciales incorrectas");
        }

        if (!Boolean.TRUE.equals(empleado.getActivo())) {
            throw new RuntimeException("Empleado inactivo en el sistema.");
        }

        Sucursal sucursal = sucursalRepo.findById(empleado.getSucursalId())
                .orElseThrow(() -> new RuntimeException("Sin sucursal asignada"));

        String token = jwtTokenProvider.createToken(empleado.getUsuario(), empleado.getEmpleado_id(), "EMPLEADO");

        return new JwtResponse(token, sucursal.getNombre(), sucursal.getSucursal_id());
    }
}