// dto/CuentaDTO.java
package com.ecusol.ecusolcore.features.bancaweb.dto;

import java.math.BigDecimal;

public record CuentaDTO(
        Long cuentaId,
        String numeroCuenta,
        BigDecimal saldo,
        String estado,
        Long tipoCuentaId
) {}