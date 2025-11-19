// modelo/Movimiento.java
package com.ecusol.ecusolcore.core.modelo;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos", schema = "ecusol")
@Data
@NoArgsConstructor
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movimiento_id;

    @Column(name = "transaccion_id")
    private Long transaccionId;

    @Column(name = "cuenta_id")
    private Long cuentaId;

    @Column(name = "tipo_movimiento")
    private String tipoMovimiento; // C = crédito, D = débito

    private BigDecimal monto;

    @Column(name = "saldo_anterior")
    private BigDecimal saldoAnterior;

    @Column(name = "saldo_nuevo")
    private BigDecimal saldoNuevo;

    private LocalDateTime fecha = LocalDateTime.now();
}