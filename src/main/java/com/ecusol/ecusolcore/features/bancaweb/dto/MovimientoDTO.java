package com.ecusol.ecusolcore.features.bancaweb.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// dto/MovimientoDTO.java
public record MovimientoDTO(
        LocalDateTime fecha,
        String tipo,
        BigDecimal monto,
        BigDecimal saldoNuevo
) {}