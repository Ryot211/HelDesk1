package com.ryot.helpdesk.dto.Ticket;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TicketSolucionDto {
    private Long ticketId;
    private Long cerradoPorId;
    private String solucion;
}
