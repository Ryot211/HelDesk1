package com.ryot.helpdesk.dto.Ticket.TicketAdjunto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketAdjuntoCrearDto {

    private Long ticketId;
    private Long subidoPorId;

    private String nombreOriginal;
    private String nombreArchivo;
    private String rutaArchivo;
    private String tipoContenido;
    private Long tamanioBytes;

}
