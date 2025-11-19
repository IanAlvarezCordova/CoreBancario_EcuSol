package com.ecusol.ecusolcore.features.bancaweb.dto;
import java.math.BigDecimal;

public record TransferenciaWebDTO(
        String cuentaOrigen,
        String cuentaDestino,
        BigDecimal monto,
        String descripcion
) {}