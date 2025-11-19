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

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest req) {
        String token = authService.login(req);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping("/login-cajero")
    public ResponseEntity<JwtResponse> loginCajero(@RequestBody com.ecusol.ecusolcore.features.auth.dto.LoginCajeroRequest req) {
        String token = authService.loginCajero(req);
        return ResponseEntity.ok(new JwtResponse(token));
    }
}