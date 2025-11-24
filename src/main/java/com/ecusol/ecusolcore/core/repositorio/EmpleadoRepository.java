//core/repositorio/EmpleadoRepository.java
package com.ecusol.ecusolcore.core.repositorio;

import com.ecusol.ecusolcore.features.administracion.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    Optional<Empleado> findByUsuario(String usuario);
}