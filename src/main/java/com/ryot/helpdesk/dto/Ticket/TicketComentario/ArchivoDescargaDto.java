package com.ryot.helpdesk.dto.Ticket.TicketComentario;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;

@Getter
@Setter
@AllArgsConstructor
public class ArchivoDescargaDto {

    private Resource resource;
    private String nombreOriginal;
    private String tipoContenido;
}