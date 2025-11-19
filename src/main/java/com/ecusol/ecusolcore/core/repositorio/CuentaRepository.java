// repositorio/CuentaRepository.java
package com.ecusol.ecusolcore.core.repositorio;

import com.ecusol.ecusolcore.core.modelo.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    List<Cuenta> findByClientePersonaId(Long clientePersonaId);
    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);
}