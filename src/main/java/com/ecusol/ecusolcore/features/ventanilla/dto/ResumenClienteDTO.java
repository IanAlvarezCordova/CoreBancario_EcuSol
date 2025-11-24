//src/features/ventanilla/dto/ResumenClienteDTO.java
package com.ecusol.ecusolcore.features.ventanilla.dto;

import java.util.List;

public record ResumenClienteDTO(
        String nombres,
        String cedula,
        String email,
        String estado, // <--- ¡ESTO FALTABA!
        List<CuentaResumenDTO> cuentas
) {}