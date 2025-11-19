// servicio/AuthService.java
package com.ecusol.ecusolcore.features.auth;

import com.ecusol.ecusolcore.config.JwtTokenProvider;
import com.ecusol.ecusolcore.features.auth.dto.LoginCajeroRequest; //
import com.ecusol.ecusolcore.features.auth.dto.LoginRequest;
import com.ecusol.ecusolcore.features.auth.dto.RegisterRequest;
import com.ecusol.ecusolcore.core.modelo.ClientePersona;
import com.ecusol.ecusolcore.core.modelo.Cuenta; //
import com.ecusol.ecusolcore.core.repositorio.ClientePersonaRepository;
import com.ecusol.ecusolcore.core.repositorio.CuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime; //

@Service
public class AuthService {

    @Autowired private ClientePersonaRepository clienteRepo;
    @Autowired private CuentaRepository cuentaRepo; //
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtTokenProvider jwtTokenProvider;

    public String register(RegisterRequest req) {
        if (clienteRepo.existsByUsuario(req.usuario())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }
        if (clienteRepo.existsByEmail(req.email())) {
            throw new RuntimeException("El email ya está registrado");
        }

        ClientePersona cliente = ClientePersona.builder()
                .cedula(req.cedula())
                .nombres(req.nombres())
                .apellidos(req.apellidos())
                .email(req.email())
                .telefono(req.telefono())
                .direccion(req.direccion())
                .usuario(req.usuario())
                .passwordHash(passwordEncoder.encode(req.password()))
                .entidadId(1L) // Banco EcuSol
                .estado("ACTIVO") // El usuario entra activo, sus cuentas no.
                .build();

        clienteRepo.save(cliente);

        return "Usuario registrado exitosamente. Ahora puede iniciar sesión.";
    }

    public String login(LoginRequest req) {
        ClientePersona cliente = clienteRepo.findByUsuario(req.usuario())
                .orElseThrow(() -> new RuntimeException("El usuario no existe")); // Mensaje específico

        if (!passwordEncoder.matches(req.password(), cliente.getPasswordHash())) {
            throw new RuntimeException("Contraseña incorrecta"); // Mensaje específico
        }

        // Actualizamos el último login
        cliente.setUltimoLogin(LocalDateTime.now());
        clienteRepo.save(cliente);

        return jwtTokenProvider.createToken(cliente.getUsuario(), cliente.getClienteId());
    }


    public String loginCajero(LoginCajeroRequest req) {
        // 1. Buscar la cuenta
        Cuenta cuenta = cuentaRepo.findByNumeroCuenta(req.numeroCuenta())
                .orElseThrow(() -> new RuntimeException("Número de cuenta no existe"));

        // 2. Buscar al dueño de esa cuenta (Foreign Key)
        ClientePersona cliente = clienteRepo.findById(cuenta.getClientePersonaId())
                .orElseThrow(() -> new RuntimeException("Cuenta sin titular asociado"));

        // 3. Validar la contraseña del dueño
        if (!passwordEncoder.matches(req.password(), cliente.getPasswordHash())) {
            throw new RuntimeException("Contraseña/PIN incorrecto");
        }

        cliente.setUltimoLogin(LocalDateTime.now());
        clienteRepo.save(cliente);

        // 4. Generar Token (El cajero necesita token para operar)
        return jwtTokenProvider.createToken(cliente.getUsuario(), cliente.getClienteId());
    }

}