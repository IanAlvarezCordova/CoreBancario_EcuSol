//src/core/repositorio/MovimientoRepository.java
package com.ecusol.ecusolcore.core.repositorio;

import com.ecusol.ecusolcore.core.modelo.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    List<Movimiento> findByCuentaIdOrderByFechaDesc(Long cuentaId);

    // Borrar todos los movimientos de una cuenta
    void deleteByCuentaId(Long cuentaId);

    // NUEVO: Borrar movimientos por ID de transacción (para limpiar referencias cruzadas)
    void deleteByTransaccionId(Long transaccionId);
}