//features/auth/dto/JwtResponse.java
package com.ecusol.ecusolcore.features.auth.dto;

public record JwtResponse(
        String token,
        String nombreSucursal, // Nuevo: Para mostrar en el front
        Long sucursalId        // Nuevo: Para enviar en transacciones
) {}