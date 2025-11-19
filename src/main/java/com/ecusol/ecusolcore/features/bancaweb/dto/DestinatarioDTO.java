package com.ecusol.ecusolcore.features.bancaweb.dto;

public record DestinatarioDTO(
        String numeroCuenta,
        String nombreTitular, // Para mostrar "JUAN PEREZ"
        String cedulaParcial  // "***1234" para seguridad
) {}