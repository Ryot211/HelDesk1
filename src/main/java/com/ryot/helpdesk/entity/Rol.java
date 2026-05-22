package com.ryot.helpdesk.entity;

import com.ryot.helpdesk.utils.EsquemaConfig;
import com.ryot.helpdesk.utils.SisVars;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "rol", schema = EsquemaConfig.helpdesk)
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String estadoRegistro;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    @PrePersist
    public void prePersist() {
        if(this.estadoRegistro == null){
            this.estadoRegistro = SisVars.Activo;
        }
        if(this.fechaCreacion == null){
            this.fechaCreacion = LocalDateTime.now();
        }
    }
    @PreUpdate
    public void preUpdate() {
        this.fechaModificacion = LocalDateTime.now();
    }


}
