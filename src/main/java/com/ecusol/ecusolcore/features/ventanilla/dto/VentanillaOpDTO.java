package com.ecusol.ecusolcore.features.ventanilla.dto;
import java.math.BigDecimal;

public record VentanillaOpDTO(
        String numeroCuenta,
        BigDecimal monto,
        String descripcion
) {}