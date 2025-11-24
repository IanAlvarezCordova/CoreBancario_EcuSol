// repositorio/ClientePersonaRepository.java
package com.ecusol.ecusolcore.core.repositorio;

import com.ecusol.ecusolcore.core.modelo.ClientePersona;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClientePersonaRepository extends JpaRepository<ClientePersona, Long> {
    Optional<ClientePersona> findByUsuario(String usuario);
    boolean existsByUsuario(String usuario);
    boolean existsByEmail(String email);

    // CORREGIDO: Debe devolver la entidad tipada
    Optional<ClientePersona> findByCedula(String cedula);
}