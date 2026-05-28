package com.ryot.helpdesk.dto.Rol;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RolDto {
    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String estadoRegistro;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaModificacion;

}
