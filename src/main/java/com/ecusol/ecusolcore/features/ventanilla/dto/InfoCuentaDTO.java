//features/ventanilla/dto/InfoCuentaDTO.java
package com.ecusol.ecusolcore.features.ventanilla.dto;

import java.math.BigDecimal;

public record InfoCuentaDTO(
        String numeroCuenta,
        String titular,
        BigDecimal saldo,
        String tipo
) {}