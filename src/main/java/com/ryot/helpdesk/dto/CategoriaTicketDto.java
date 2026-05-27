package com.ryot.helpdesk.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CategoriaTicketDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private String estadoRegistro;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}
