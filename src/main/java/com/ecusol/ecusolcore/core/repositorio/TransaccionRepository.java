package com.ecusol.ecusolcore.core.repositorio;

import com.ecusol.ecusolcore.core.modelo.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {}