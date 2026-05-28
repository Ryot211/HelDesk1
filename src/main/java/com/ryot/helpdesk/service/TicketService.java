package com.ryot.helpdesk.service;

import com.ryot.helpdesk.dto.Ticket.*;
import com.ryot.helpdesk.dto.Ticket.TicketComentario.TicketComentarioCrearDto;
import com.ryot.helpdesk.dto.Ticket.TicketComentario.TicketComentarioDto;
import com.ryot.helpdesk.entity.*;
import com.ryot.helpdesk.mapper.TicketComentarioMapper;
import com.ryot.helpdesk.mapper.TicketMapper;
import com.ryot.helpdesk.repository.*;
import com.ryot.helpdesk.utils.SisVars;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    @Autowired
    private TicketRepo ticketRepo;
    @Autowired
    private CategoriaTicketRepo categoriaTicketRepo;
    @Autowired
    private DepartamentoRepo departamentoRepo;
    @Autowired
    private UsuarioRepo usuarioRepo;
    @Autowired
    private TicketMapper ticketMapper;
    @Autowired
    private TicketComentarioRepo ticketComentarioRepo;
    @Autowired
    private TicketComentarioMapper ticketComentarioMapper;

    public List<TicketDto> listarTodos(){
        List<Ticket> tickets = ticketRepo.findAllByOrderByIdDesc();
        return ticketMapper.toDtos(tickets);
    }

    public List<TicketDto> listarPorEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            throw new RuntimeException("El estado es obligatorio");
        }

        List<Ticket> tickets = ticketRepo.findByEstadoOrderByIdDesc(estado);
        return ticketMapper.toDtos(tickets);
    }
    public TicketDto buscarPorId(Long id) {
        Ticket ticket = ticketRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe el ticket con id: " + id));

        return ticketMapper.toDto(ticket);
    }

    public TicketDto crear(TicketCrearDto dto) {
        validarCrear(dto);

        CategoriaTicket categoria = categoriaTicketRepo.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("No existe la categoría con id: " + dto.getCategoriaId()));

        Departamento departamento = departamentoRepo.findById(dto.getDepartamentoSolicitanteId())
                .orElseThrow(() -> new RuntimeException("No existe el departamento con id: " + dto.getDepartamentoSolicitanteId()));

        Usuario creadoPor = usuarioRepo.findById(dto.getCreadoPorId())
                .orElseThrow(() -> new RuntimeException("No existe el usuario creador con id: " + dto.getCreadoPorId()));

        Ticket ticket = new Ticket();
        ticket.setCodigo(generarCodigoTicket());
        ticket.setTitulo(dto.getTitulo());
        ticket.setDescripcion(dto.getDescripcion());
        ticket.setPrioridad(dto.getPrioridad() == null || dto.getPrioridad().isBlank() ? "MEDIA" : dto.getPrioridad());
        ticket.setEstado("REGISTRADO");
        ticket.setCategoria(categoria);
        ticket.setDepartamentoSolicitante(departamento);
        ticket.setCreadoPor(creadoPor);
        ticket.setFechaCreacion(LocalDateTime.now());

        Ticket guardado = ticketRepo.save(ticket);
        return ticketMapper.toDto(guardado);
    }
    public TicketDto asignar(TicketAsignarDto dto) {
        if (dto.getTicketId() == null) {
            throw new RuntimeException("El id del ticket es obligatorio");
        }

        if (dto.getAsignadoId() == null) {
            throw new RuntimeException("El usuario asignado es obligatorio");
        }

        Ticket ticket = ticketRepo.findById(dto.getTicketId())
                .orElseThrow(() -> new RuntimeException("No existe el ticket con id: " + dto.getTicketId()));

        Usuario asignadoA = usuarioRepo.findById(dto.getAsignadoId())
                .orElseThrow(() -> new RuntimeException("No existe el usuario asignado con id: " + dto.getAsignadoId()));

        ticket.setAsignadoA(asignadoA);
        ticket.setEstado("ASIGNADO");
        ticket.setFechaAsignacion(LocalDateTime.now());

        Ticket actualizado = ticketRepo.save(ticket);
        return ticketMapper.toDto(actualizado);
    }

    public TicketDto cambiarEstado(TicketEstadoDto dto) {
        if (dto.getTicketId() == null) {
            throw new RuntimeException("El id del ticket es obligatorio");
        }

        if (dto.getEstado() == null || dto.getEstado().isBlank()) {
            throw new RuntimeException("El estado es obligatorio");
        }

        validarEstado(dto.getEstado());

        Ticket ticket = ticketRepo.findById(dto.getTicketId())
                .orElseThrow(() -> new RuntimeException("No existe el ticket con id: " + dto.getTicketId()));

        ticket.setEstado(dto.getEstado());

        if (SisVars.EN_PROCESO.equals(dto.getEstado()) && ticket.getFechaInicioAtencion() == null) {
            ticket.setFechaInicioAtencion(LocalDateTime.now());
        }

        if (SisVars.RESUELTO.equals(dto.getEstado())) {
            ticket.setFechaResolucion(LocalDateTime.now());
        }

        if (SisVars.CERRADO.equals(dto.getEstado())) {
            ticket.setFechaCierre(LocalDateTime.now());
        }

        Ticket actualizado = ticketRepo.save(ticket);
        return ticketMapper.toDto(actualizado);
    }


    public TicketDto cerrarConSolucion(TicketSolucionDto dto) {
        if (dto.getTicketId() == null) {
            throw new RuntimeException("El id del ticket es obligatorio");
        }

        if (dto.getCerradoPorId() == null) {
            throw new RuntimeException("El usuario que cierra el ticket es obligatorio");
        }

        if (dto.getSolucion() == null || dto.getSolucion().isBlank()) {
            throw new RuntimeException("La solución es obligatoria");
        }

        Ticket ticket = ticketRepo.findById(dto.getTicketId())
                .orElseThrow(() -> new RuntimeException("No existe el ticket con id: " + dto.getTicketId()));

        Usuario cerradoPor = usuarioRepo.findById(dto.getCerradoPorId())
                .orElseThrow(() -> new RuntimeException("No existe el usuario con id: " + dto.getCerradoPorId()));

        ticket.setSolucion(dto.getSolucion());
        ticket.setCerradoPor(cerradoPor);
        ticket.setEstado(SisVars.CERRADO);

        if (ticket.getFechaResolucion() == null) {
            ticket.setFechaResolucion(LocalDateTime.now());
        }

        ticket.setFechaCierre(LocalDateTime.now());

        Ticket actualizado = ticketRepo.save(ticket);
        return ticketMapper.toDto(actualizado);
    }

    private void validarCrear(TicketCrearDto dto) {
        if (dto.getTitulo() == null || dto.getTitulo().isBlank()) {
            throw new RuntimeException("El título del ticket es obligatorio");
        }

        if (dto.getDescripcion() == null || dto.getDescripcion().isBlank()) {
            throw new RuntimeException("La descripción del ticket es obligatoria");
        }

        if (dto.getCategoriaId() == null) {
            throw new RuntimeException("La categoría del ticket es obligatoria");
        }

        if (dto.getDepartamentoSolicitanteId() == null) {
            throw new RuntimeException("El departamento solicitante es obligatorio");
        }

        if (dto.getCreadoPorId() == null) {
            throw new RuntimeException("El usuario creador es obligatorio");
        }
    }

    private void validarEstado(String estado) {
        List<String> estadosPermitidos = List.of(
                SisVars.REGISTRADO,
                SisVars.ASIGNADO,
                SisVars.EN_PROCESO,
                SisVars.RESUELTO,
                SisVars.CERRADO,
                SisVars.ANULADO,
                SisVars.REABIERTO
        );

        if (!estadosPermitidos.contains(estado)) {
            throw new RuntimeException("Estado no permitido: " + estado);
        }
    }

    private String generarCodigoTicket() {
        int anio = LocalDate.now().getYear();
        long secuencial = ticketRepo.count() + 1;

        String codigo;

        do {
            codigo = String.format("HD-%d-%06d", anio, secuencial);
            secuencial++;
        } while (ticketRepo.existsByCodigo(codigo));

        return codigo;
    }

//Ticket comentario

    @Transactional(readOnly = true)
    public List<TicketComentarioDto> listarComentarios(Long ticketId) {
        if (ticketId == null) {
            throw new RuntimeException("El id del ticket es obligatorio");
        }

        List<TicketComentario> comentarios =
                ticketComentarioRepo.findByTicketIdOrderByFechaCreacionAsc(ticketId);

        return ticketComentarioMapper.toDto(comentarios);
    }

    public TicketComentarioDto agregarComentario(TicketComentarioCrearDto dto) {


        validarCrearComentario(dto);

        Ticket ticket = ticketRepo.findById(dto.getTicketId())
                .orElseThrow(() -> new RuntimeException("No existe el ticket con id: " + dto.getTicketId()));

        Usuario usuario = usuarioRepo.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("No existe el usuario con id: " + dto.getUsuarioId()));

        TicketComentario comentario = new TicketComentario();
        comentario.setTicket(ticket);
        comentario.setUsuario(usuario);
        comentario.setComentario(dto.getComentario());
        comentario.setTipo(
                dto.getTipo() == null || dto.getTipo().isBlank()
                        ? "PUBLICO"
                        : dto.getTipo()
        );

        TicketComentario guardado = ticketComentarioRepo.save(comentario);
        return ticketComentarioMapper.toDto(guardado);
    }


    private void validarCrearComentario(TicketComentarioCrearDto dto) {
        if (dto.getTicketId() == null) {
            throw new RuntimeException("El ticket es obligatorio");
        }

        if (dto.getUsuarioId() == null) {
            throw new RuntimeException("El usuario es obligatorio");
        }

        if (dto.getComentario() == null || dto.getComentario().isBlank()) {
            throw new RuntimeException("El comentario es obligatorio");
        }

        if (dto.getTipo() != null && !dto.getTipo().isBlank()) {
            validarTipoComentario(dto.getTipo());
        }
    }

    private void validarTipoComentario(String tipo) {
        List<String> tiposPermitidos = List.of(
                SisVars.PUBLICO,
                SisVars.INTERNO,
                SisVars.SISTEMA
        );

        if (!tiposPermitidos.contains(tipo)) {
            throw new RuntimeException("Tipo de comentario no permitido: " + tipo);
        }
    }
}
