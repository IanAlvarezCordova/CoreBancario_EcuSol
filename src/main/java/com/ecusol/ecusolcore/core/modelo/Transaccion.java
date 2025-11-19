// modelo/Transaccion.java
package com.ecusol.ecusolcore.core.modelo;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacciones", schema = "ecusol")
@Data
@NoArgsConstructor
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transaccion_id;

    private String referencia;
    private String tipo;      // DEPOSITO, RETIRO, TRANSFERENCIA
    private String canal;     // WEB o CAJERO
    private BigDecimal monto;
    private String descripcion;
    private String estado = "COMPLETADA";

    @Column(name = "fecha_ejecucion")
    private LocalDateTime fechaEjecucion = LocalDateTime.now();

    @Column(name = "cuenta_origen_id")
    private Long cuentaOrigenId;

    @Column(name = "cuenta_destino_id")
    private Long cuentaDestinoId;

    @Column(name = "sucursal_id")
    private Long sucursalId;
}