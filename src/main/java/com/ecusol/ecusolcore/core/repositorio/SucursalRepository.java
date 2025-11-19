// repositorio/SucursalRepository.java
package com.ecusol.ecusolcore.core.repositorio;

import com.ecusol.ecusolcore.core.modelo.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SucursalRepository extends JpaRepository<Sucursal, Long> {
}