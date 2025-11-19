// modelo/Sucursal.java
package com.ecusol.ecusolcore.core.modelo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "sucursales", schema = "ecusol")
@Data
public class Sucursal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sucursal_id;

    private String nombre;
}