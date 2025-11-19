package com.ecusol.ecusolcore.features.auth.dto;

public record LoginCajeroRequest(
        String numeroCuenta,  // Actúa como el "Usuario"
        String password       // Actúa como el "PIN"
) {}