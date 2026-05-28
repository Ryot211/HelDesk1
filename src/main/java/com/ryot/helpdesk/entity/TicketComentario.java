package com.ryot.helpdesk.entity;

import com.ryot.helpdesk.utils.EsquemaConfig;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "ticket_comentario", schema = EsquemaConfig.helpdesk)
public class TicketComentario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", referencedColumnName = "id")
    private Ticket ticket;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn  (name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;
    private String comentario;
    private String tipo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;


}
