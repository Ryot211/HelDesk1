package com.ryot.helpdesk.dto.Ticket;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DashboardResumenDto {

    private Long totalTickets;
    private Long ticketsAbiertos;
    private Long ticketsEnAtencion;
    private Long ticketsCerrados;
    private Long ticketsFinalizados;
}
