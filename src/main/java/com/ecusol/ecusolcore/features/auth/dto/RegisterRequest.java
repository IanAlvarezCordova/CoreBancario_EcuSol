//features/auth/dto/RegisterRequest.java
package com.ecusol.ecusolcore.features.auth.dto;

// dto/RegisterRequest.java
public record RegisterRequest(
        String cedula,
        String nombres,
        String apellidos,
        String email,
        String usuario,
        String password,
        String telefono,   // opcional
        String direccion   // opcional
) {}
