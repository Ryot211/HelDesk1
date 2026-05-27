package com.ryot.helpdesk.entity;

import com.ryot.helpdesk.utils.EsquemaConfig;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Table(name = "usuario", schema = EsquemaConfig.helpdesk)
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn (name = "rol_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Rol rol;
    @JoinColumn(name = "departamento_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Departamento departamento;
    private String nombres;
    private String apellidos;
    private String email;
    private String passwordHash;
    private String telefono;
    private String estadoRegistro;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

}
