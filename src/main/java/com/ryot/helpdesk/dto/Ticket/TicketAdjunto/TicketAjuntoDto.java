package com.ryot.helpdesk.dto.Ticket.TicketAdjunto;

import com.ryot.helpdesk.dto.Ticket.TicketDto;
import com.ryot.helpdesk.dto.Usuario.UsuarioDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TicketAjuntoDto {

    private Long id;
    private TicketDto ticket;
    private UsuarioDto subidoPor;

    private String nombreOriginal;
    private String nombreArchivo;
    private String rutaArchivo;
    private String tipoContenido;
    private Long tamanioBytes;

    private String estadoRegistro;
    private LocalDateTime fechaCreacion;
}
