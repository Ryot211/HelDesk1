package com.ryot.helpdesk.service;

import com.ryot.helpdesk.dto.Ticket.*;
import com.ryot.helpdesk.dto.Ticket.TicketAdjunto.TicketAdjuntoCrearDto;
import com.ryot.helpdesk.dto.Ticket.TicketAdjunto.TicketAjuntoDto;
import com.ryot.helpdesk.dto.Ticket.TicketComentario.TicketComentarioCrearDto;
import com.ryot.helpdesk.dto.Ticket.TicketComentario.TicketComentarioDto;
import com.ryot.helpdesk.entity.*;
import com.ryot.helpdesk.mapper.TicketAdjuntoMapper;
import com.ryot.helpdesk.mapper.TicketComentarioMapper;
import com.ryot.helpdesk.mapper.TicketHistorialMapper;
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
    @Autowired
    private TicketAdjuntoRepo ticketAdjuntoRepo;
    @Autowired
    private TicketAdjuntoMapper ticketAdjuntoMapper;
    @Autowired
    private TicketHistorialRepo ticketHistorialRepo;
    @Autowired
    private TicketHistorialMapper  ticketHistorialMapper;

    @Transactional (readOnly = true)
    public List<TicketDto> listarTodos(){
        List<Ticket> tickets = ticketRepo.findAllByOrderByIdDesc();
        return ticketMapper.toDtos(tickets);
    }
    @Transactional (readOnly = true)
    public List<TicketDto> listarPorEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            throw new RuntimeException("El estado es obligatorio");
        }

        List<Ticket> tickets = ticketRepo.findByEstadoOrderByIdDesc(estado);
        return ticketMapper.toDtos(tickets);
    }

    @Transactional (readOnly = true)
    public TicketDto buscarPorId(Long id) {
        Ticket ticket = ticketRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe el ticket con id: " + id));

        return ticketMapper.toDto(ticket);
    }
    @Transactional
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
        ticket.setEstado(SisVars.REGISTRADO);
        ticket.setCategoria(categoria);
        ticket.setDepartamento(departamento);
        ticket.setUsuarioCreacion(creadoPor);
        ticket.setFechaCreacion(LocalDateTime.now());

        Ticket guardado = ticketRepo.save(ticket);
        registrarHistorial(
                guardado,
                creadoPor,
                SisVars.HIST_CREACION,
                null,
                guardado.getEstado(),
                null,
                guardado.getPrioridad(),
                null,
                SisVars.HIST_CREACION
        );

        return ticketMapper.toDto(guardado);
    }

    @Transactional
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
        String estadoAnterior = ticket.getEstado();
        ticket.setUsuarioAsignado(asignadoA);
        ticket.setEstado(SisVars.ASIGNADO);
        ticket.setFechaAsignacion(LocalDateTime.now());

        Ticket actualizado = ticketRepo.save(ticket);

        registrarHistorial(
                actualizado,
                asignadoA,
                SisVars.HIST_ASIGNACION,
                estadoAnterior,
                actualizado.getEstado(),
                null,
                null,
                asignadoA,
                SisVars.HIST_ASIGNACION
        );
        return ticketMapper.toDto(actualizado);
    }
    @Transactional
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
        String estadoAnterior = ticket.getEstado();
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
        Usuario usuarioAccion = actualizado.getUsuarioAsignado() != null
                ? actualizado.getUsuarioAsignado()
                : actualizado.getUsuarioCreacion();
        registrarHistorial(
                actualizado,
                usuarioAccion,
                SisVars.HIST_CAMBIO_ESTADO,
                estadoAnterior,
                actualizado.getEstado(),
                null,
                null,
                actualizado.getUsuarioAsignado(),
                SisVars.OBS_CAMBIO_ESTADO
        );

        return ticketMapper.toDto(actualizado);
    }

    @Transactional
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
        String estadoAnterior = ticket.getEstado();

        ticket.setSolucion(dto.getSolucion());
        ticket.setUsuarioFinalizado(cerradoPor);

        ticket.setEstado(SisVars.CERRADO);

        if (ticket.getFechaResolucion() == null) {
            ticket.setFechaResolucion(LocalDateTime.now());
        }

        ticket.setFechaCierre(LocalDateTime.now());

        Ticket actualizado = ticketRepo.save(ticket);
        registrarHistorial(
                actualizado,
                cerradoPor,
                SisVars.HIST_CIERRE,
                estadoAnterior,
                actualizado.getEstado(),
                null,
                null,
                actualizado.getUsuarioAsignado(),
                SisVars.OBS_TICKET_CERRADO_SOLUCION
        );


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
    @Transactional
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
        comentario.setFechaCreacion(LocalDateTime.now());
        comentario.setTipo(
                dto.getTipo() == null || dto.getTipo().isBlank()
                        ? SisVars.PUBLICO
                        : dto.getTipo()
        );

        TicketComentario guardado = ticketComentarioRepo.save(comentario);
        registrarHistorial(
                ticket,
                usuario,
                SisVars.HIST_COMENTARIO,
                null,
                null,
                null,
                null,
                ticket.getUsuarioAsignado(),
                SisVars.OBS_COMENTARIO_AGREGADO
        );
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

//    Ticket Adjunto

    @Transactional(readOnly = true)
    public List<TicketAjuntoDto> listarAdjuntos(Long ticketId) {
        if (ticketId == null) {
            throw new RuntimeException("El id del ticket es obligatorio");
        }

        List<TicketAdjunto> adjuntos =
                ticketAdjuntoRepo.findByTicketIdAndEstadoRegistroOrderByFechaCreacionDesc(
                        ticketId,
                        SisVars.Activo
                );

        return ticketAdjuntoMapper.toDto(adjuntos);
    }

    @Transactional
    public TicketAjuntoDto registrarAdjunto(TicketAdjuntoCrearDto dto) {
        validarRegistrarAdjunto(dto);

        Ticket ticket = ticketRepo.findById(dto.getTicketId())
                .orElseThrow(() -> new RuntimeException("No existe el ticket con id: " + dto.getTicketId()));

        Usuario subidoPor = usuarioRepo.findById(dto.getSubidoPorId())
                .orElseThrow(() -> new RuntimeException("No existe el usuario con id: " + dto.getSubidoPorId()));

        TicketAdjunto adjunto = new TicketAdjunto();
        adjunto.setTicket(ticket);
        adjunto.setUsuario(subidoPor);
        adjunto.setNombreOriginal(dto.getNombreOriginal());
        adjunto.setNombreAchivo(dto.getNombreArchivo());
        adjunto.setRutaArchivo(dto.getRutaArchivo());
        adjunto.setTipoContenido(dto.getTipoContenido());
        adjunto.setTamanioBytes(dto.getTamanioBytes());
        adjunto.setEstadoRegistro(SisVars.Activo);
        adjunto.setFechaCreacion(LocalDateTime.now());

        TicketAdjunto guardado = ticketAdjuntoRepo.save(adjunto);
        registrarHistorial(
                ticket,
                subidoPor,
                SisVars.HIST_ADJUNTO,
                null,
                null,
                null,
                null,
                ticket.getUsuarioAsignado(),
                SisVars.OBS_ADJUNTO_REGISTRADO

        );
        return ticketAdjuntoMapper.toDto(guardado);
    }

    @Transactional
    public void inactivarAdjunto(Long id) {
        if (id == null) {
            throw new RuntimeException("El id del adjunto es obligatorio");
        }

        TicketAdjunto adjunto = ticketAdjuntoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe el adjunto con id: " + id));

        adjunto.setEstadoRegistro(SisVars.INACTIVO);
        adjunto.setFechaCreacion(LocalDateTime.now());
        registrarHistorial(
                adjunto.getTicket(),
                adjunto.getUsuario(),
                SisVars.HIST_ADJUNTO,
                null,
                null,
                null,
                null,
                adjunto.getTicket().getUsuarioAsignado(),
                "Adjunto inactivado"
        );

        ticketAdjuntoRepo.save(adjunto);
    }

    private void validarRegistrarAdjunto(TicketAdjuntoCrearDto dto) {
        if (dto.getTicketId() == null) {
            throw new RuntimeException("El ticket es obligatorio");
        }

        if (dto.getSubidoPorId() == null) {
            throw new RuntimeException("El usuario que sube el archivo es obligatorio");
        }

        if (dto.getNombreOriginal() == null || dto.getNombreOriginal().isBlank()) {
            throw new RuntimeException("El nombre original del archivo es obligatorio");
        }

        if (dto.getNombreArchivo() == null || dto.getNombreArchivo().isBlank()) {
            throw new RuntimeException("El nombre interno del archivo es obligatorio");
        }

        if (dto.getRutaArchivo() == null || dto.getRutaArchivo().isBlank()) {
            throw new RuntimeException("La ruta del archivo es obligatoria");
        }
    }
//Ticket Historial

    @Transactional(readOnly = true)
    public List<TicketHistorialDto> listarHistorial(Long ticketId) {
        if(ticketId == null) {
            throw new RuntimeException("El id del ticket es obligatorio");
        }

        List<TicketHistorial> historial =
                    ticketHistorialRepo.findByTicketIdOrderByFechaCreacionDesc(ticketId);
        return ticketHistorialMapper.toDto(historial);
    }

    private void registrarHistorial(
            Ticket ticket,
            Usuario usuario,
            String accion,
            String estadoAnterior,
            String estadoNuevo,
            String prioridadAnterior,
            String prioridadNueva,
            Usuario usuarioAsignado,
            String observacion
    ){
        TicketHistorial historial = new TicketHistorial();
        historial.setTicket(ticket);
        historial.setUsuario(usuario);
        historial.setAccion(accion);

        historial.setEstadoAnteior(estadoAnterior);
        historial.setEstadoNuevo(estadoNuevo);
        historial.setPrioridadAnterior(prioridadAnterior);
        historial.setPrioridadNueva(prioridadNueva);

        historial.setUsuarioAsignado(usuarioAsignado);
        historial.setFechaCreacion(LocalDateTime.now());

        historial.setObservacion(observacion);
        ticketHistorialRepo.save(historial);
    }
}
