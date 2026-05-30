package com.ryot.helpdesk.dto.Ticket;

import com.ryot.helpdesk.dto.Usuario.UsuarioDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
public class TicketHistorialDto {
    private Long id;
    private TicketDto ticket;
    private UsuarioDto usuario;

    private String accion;
    private String estadoAnterior;
    private String estadoNuevo;

    private String prioridadAnterior;
    private String prioridadNueva;

    private UsuarioDto usuarioAsignado;

    private String observacion;

    private LocalDateTime fechaCreacion;

}
