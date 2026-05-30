package com.ryot.helpdesk.entity;

import com.ryot.helpdesk.utils.EsquemaConfig;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table (name = "ticket_historial", schema = EsquemaConfig.helpdesk)
public class TicketHistorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", referencedColumnName = "id")
    private Ticket ticket;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn  (name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;
    private String accion;
    private String estadoAnterior;
    private String estadoNuevo;
    private String prioridadAnterior;
    private String prioridadNueva;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn  (name = "usuario_asignado_nuevo", referencedColumnName = "id")
    private Usuario usuarioAsignado;
    private String observacion;
    private LocalDateTime fechaCreacion;


}
