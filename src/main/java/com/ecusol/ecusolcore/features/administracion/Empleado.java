//features/administracion/Empleado.java
package com.ecusol.ecusolcore.features.administracion;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "empleados", schema = "ecusol")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long empleado_id;

    @Column(unique = true, nullable = false)
    private String usuario;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    private String nombres;
    private String rol; // 'CAJERO', 'ADMIN'
    private Boolean activo = true;

    @Column(name = "sucursal_id")
    private Long sucursalId;
}