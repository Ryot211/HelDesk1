package com.ryot.helpdesk.dto.Ticket.TicketComentario;

import com.ryot.helpdesk.dto.Ticket.TicketDto;
import com.ryot.helpdesk.dto.Usuario.UsuarioDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
public class TicketComentarioDto {
    private Long id;

    private TicketDto ticket;
    private UsuarioDto usuario;

    private String comentario;
    private String tipo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}
