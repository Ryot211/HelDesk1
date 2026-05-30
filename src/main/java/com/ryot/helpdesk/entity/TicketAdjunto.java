package com.ryot.helpdesk.entity;

import com.ryot.helpdesk.utils.EsquemaConfig;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name="ticket_adjunto", schema = EsquemaConfig.helpdesk)
public class TicketAdjunto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", referencedColumnName = "id")
    private Ticket ticket;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_creacion", referencedColumnName = "id")
    private Usuario usuario;
    private String nombreOriginal;
    private String nombreAchivo;
    private String rutaArchivo;
    private String tipoContenido;
    private  Long tamanioBytes;
    private String estadoRegistro;
    private LocalDateTime fechaCreacion;

}
