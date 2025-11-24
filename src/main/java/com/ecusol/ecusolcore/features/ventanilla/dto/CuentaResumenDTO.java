//src/features/ventanilla/dto/CuentaResumenDTO.java
package com.ecusol.ecusolcore.features.ventanilla.dto;

import java.math.BigDecimal;

public record CuentaResumenDTO(
        String numeroCuenta,
        String tipo, // AHORROS / CORRIENTE
        BigDecimal saldo,
        String estado
) {}