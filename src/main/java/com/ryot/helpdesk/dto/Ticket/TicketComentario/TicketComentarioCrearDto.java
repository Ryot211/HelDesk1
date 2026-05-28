package com.ryot.helpdesk.dto.Ticket.TicketComentario;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketComentarioCrearDto {
    private Long ticketId;
    private Long usuarioId;

    private String comentario;
    private String tipo;

}
