//core/repositorio/TransaccionRepository.java
package com.ecusol.ecusolcore.core.repositorio;

import com.ecusol.ecusolcore.core.modelo.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    // Buscar transacciones donde la cuenta fue origen
    List<Transaccion> findByCuentaOrigenId(Long id);

    // Buscar transacciones donde la cuenta fue destino
    List<Transaccion> findByCuentaDestinoId(Long id);
}