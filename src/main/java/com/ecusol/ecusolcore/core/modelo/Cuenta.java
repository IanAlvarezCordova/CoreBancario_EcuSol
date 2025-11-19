// modelo/Cuenta.java
package com.ecusol.ecusolcore.core.modelo;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cuentas", schema = "ecusol")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cuenta_id")
    private Long cuentaId;

    @Column(name = "entidad_id", nullable = false)
    private Long entidadId;                  // ← NUEVO

    @Column(name = "tipo_cuenta_id", nullable = false)
    private Long tipoCuentaId;               // ← NUEVO

    @Column(name = "cliente_persona_id")
    private Long clientePersonaId;           // ← CAMBIADO: ahora es Long (no objeto)

    @Column(name = "empresa_id")
    private Long empresaId;                  // ← NUEVO (puede ser null)

    @Column(name = "numero_cuenta", unique = true, nullable = false)
    private String numeroCuenta;

    private BigDecimal saldo = BigDecimal.ZERO;

    @Column(name = "fecha_apertura")
    private LocalDate fechaApertura = LocalDate.now();  // ← NUEVO

    private String estado = "ACTIVA";
}