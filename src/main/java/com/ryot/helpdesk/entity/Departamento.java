package com.ryot.helpdesk.entity;


import com.ryot.helpdesk.utils.EsquemaConfig;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table (name= "departamento" , schema = EsquemaConfig.helpdesk)

public class Departamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String descripcion;
    private String estadoRegistro;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

}
