package com.ryot.helpdesk.dto.Ticket;


import com.ryot.helpdesk.dto.Categoria.CategoriaTicketDto;
import com.ryot.helpdesk.dto.Departamento.DepartamentoDto;
import com.ryot.helpdesk.dto.Usuario.UsuarioDto;
import com.ryot.helpdesk.entity.CategoriaTicket;
import com.ryot.helpdesk.entity.Departamento;
import com.ryot.helpdesk.entity.Usuario;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
public class TicketDto {
    private Long id;
    private String codigo;
    private String titulo;
    private String descripcion;
    private String estado;
    private String prioridad;
    private CategoriaTicketDto categoria;
    private DepartamentoDto departamento;
    private UsuarioDto usuarioCreacion;
    private UsuarioDto usuarioAsignado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaInicioAtencion;
    private LocalDateTime fechaResolucion;
    private LocalDateTime fechaCierre;
    private UsuarioDto usuarioFinalizado;
    private String solucion;
}
