package com.ryot.helpdesk.dto.Ticket;


import com.ryot.helpdesk.entity.CategoriaTicket;
import com.ryot.helpdesk.entity.Departamento;
import com.ryot.helpdesk.entity.Usuario;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
public class TicketDto {
    private Long id;
    private String codigo;
    private String titulo;
    private String descripcion;
    private String estado;
    private String prioridad;
    private CategoriaTicket categoria;
    private Departamento departamento;
    private Usuario usuarioCreacion;
    private Usuario usuarioAsignado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaInicioAtencion;
    private LocalDateTime fechaResolucion;
    private LocalDateTime fechaCierre;
    private Usuario usuarioFinalizado;
    private String solucion;
}
