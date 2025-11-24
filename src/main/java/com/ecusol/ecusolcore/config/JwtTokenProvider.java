// config/JwtTokenProvider.java
package com.ecusol.ecusolcore.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key = Keys.hmacShaKeyFor(
            "MiClaveSecretaSuperLarga2025EcuSolBank1234567890abcdef".getBytes());

    @Value("${jwt.expiration-ms}")
    private long validityInMilliseconds;

    // MODIFICADO: Recibe el ROL
    public String createToken(String username, Long id, String rol) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .subject(username)
                .claim("id", id)
                .claim("rol", rol) // 'CLIENTE' o 'EMPLEADO'
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    public String getUsername(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }

    // Validaciones genéricas
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) { return false; }
    }
    // Obtener ID desde el token
    public Long getId(String token) {
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return claims.get("id", Long.class);
    }
}

