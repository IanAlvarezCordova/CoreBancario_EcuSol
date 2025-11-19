// modelo/ClientePersona.java
package com.ecusol.ecusolcore.core.modelo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "clientes_persona", schema = "ecusol")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientePersona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(name = "entidad_id", nullable = false)
    private Long entidadId = 1L;  // Siempre EcuSol

    @Column(unique = true, nullable = false)
    private String cedula;

    private String nombres;
    private String apellidos;
    private String email;

    @Column(unique = true, nullable = false)
    private String usuario;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    private String estado = "ACTIVO";

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "ultimo_login")
    private java.time.LocalDateTime ultimoLogin;

    @Column(name = "direccion")
    private String direccion;


}