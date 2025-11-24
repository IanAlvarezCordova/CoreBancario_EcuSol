// controlador/auth/AuthControlador.java
package com.ecusol.ecusolcore.features.auth;

import com.ecusol.ecusolcore.features.auth.dto.JwtResponse;
import com.ecusol.ecusolcore.features.auth.dto.LoginRequest;
import com.ecusol.ecusolcore.features.auth.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthControlador {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }
    @PostMapping("/login/web")
    public ResponseEntity<JwtResponse> loginWeb(@RequestBody LoginRequest req) {
        // 1. Obtenemos solo el token del servicio (String)
        String token = authService.loginCliente(req);

        // 2. Construimos la respuesta llenando los datos de sucursal manualmente
        // "Banca Web" es informativo y el ID es null porque no aplica
        return ResponseEntity.ok(new JwtResponse(token, "Banca Web", null));
    }

    @PostMapping("/login/ventanilla")
    public ResponseEntity<JwtResponse> loginVentanilla(@RequestBody LoginRequest req) {
        // AuthService ahora devuelve el objeto JwtResponse completo, no solo el string
        return ResponseEntity.ok(authService.loginEmpleado(req));
    }
}