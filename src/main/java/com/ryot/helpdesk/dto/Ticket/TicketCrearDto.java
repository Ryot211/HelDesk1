package com.ryot.helpdesk.dto.Ticket;

import lombok.*;

@Getter
@Setter
public class TicketCrearDto {
    private String titulo;
    private String descripcion;
    private String prioridad;

    private Long categoriaId;
    private Long departamentoSolicitanteId;
    private Long creadoPorId;
}
