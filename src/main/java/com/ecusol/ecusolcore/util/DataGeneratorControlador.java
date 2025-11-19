// controlador/util/DataGeneratorControlador.java
package com.ecusol.ecusolcore.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/util")
public class DataGeneratorControlador {

    @Autowired
    private DataGeneratorService generator;

    @PostMapping("/generar-datos/{cantidad}")
    public String generar(@PathVariable int cantidad) {
        return generator.generarClientesConCuentas(cantidad);
    }
}