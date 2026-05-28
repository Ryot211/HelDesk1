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
    private Departamento departamentoSolicitante;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por_id", referencedColumnName = "id", nullable = false)
    private Usuario creadoPor;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignado_a_id", referencedColumnName = "id")
    private Usuario asignadoA;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaInicioAtencion;
    private LocalDateTime fechaResolucion;
    private LocalDateTime fechaCierre;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cerrado_por_id", referencedColumnName = "id")
    private Usuario cerradoPor;
    private String solucion;

}
