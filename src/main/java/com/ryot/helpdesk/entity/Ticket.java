package com.ryot.helpdesk.entity;

import com.ryot.helpdesk.utils.EsquemaConfig;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ticket", schema = EsquemaConfig.helpdesk)
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String codigo;
    private String titulo;
    private String descripcion;
    private String estado;
    private String prioridad;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", referencedColumnName = "id", nullable = false)
    private CategoriaTicket categoria;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id", referencedColumnName = "id")
    private Departamento departamento;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_creacion", referencedColumnName = "id", nullable = false)
    private Usuario usuarioCreacion;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_asignado", referencedColumnName = "id")
    private Usuario usuarioAsignado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaInicioAtencion;
    private LocalDateTime fechaResolucion;
    private LocalDateTime fechaCierre;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_finalizado", referencedColumnName = "id")
    private Usuario usuarioFinalizado;
    private String solucion;

}
