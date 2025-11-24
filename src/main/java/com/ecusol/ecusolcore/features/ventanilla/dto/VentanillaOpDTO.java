//features/ventanilla/dto/VentanillaOpDTO.java
package com.ecusol.ecusolcore.features.ventanilla.dto;
import java.math.BigDecimal;

public record VentanillaOpDTO(
        String numeroCuentaOrigen,  // La cuenta del cliente que está en ventanilla
        String numeroCuentaDestino, // Opcional (solo para transferencias)
        BigDecimal monto,
        String descripcion
) {}