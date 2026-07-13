package com.ryot.helpdesk.dto.Ticket;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor

public class ArchivoGuardadoDto {
    private String nombreOriginal;
    private String nombreArchivo;
    private String rutaArchivo;
    private String tipoContenido;
    private Long tamanioBytes;
}
