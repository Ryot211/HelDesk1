package com.ryot.helpdesk.controller;


import com.ryot.helpdesk.dto.Ticket.*;
import com.ryot.helpdesk.dto.Ticket.TicketAdjunto.TicketAdjuntoCrearDto;
import com.ryot.helpdesk.dto.Ticket.TicketAdjunto.TicketAjuntoDto;
import com.ryot.helpdesk.dto.Ticket.TicketComentario.TicketComentarioCrearDto;
import com.ryot.helpdesk.dto.Ticket.TicketComentario.TicketComentarioDto;
import com.ryot.helpdesk.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @PostMapping("/listar")
    public List<TicketDto> listarTodos(){
        return ticketService.listarTodos();
    }

    @PostMapping("/listar-por-estado")
    public List<TicketDto> listarPorEstado(@PathVariable String estado){
        return ticketService.listarPorEstado(estado);
    }

    @PostMapping("/buscar")
    public TicketDto buscar(@PathVariable Long id){
        return ticketService.buscarPorId(id);
    }

    @PostMapping("/crear")
    public TicketDto crear(@RequestBody TicketCrearDto dto){
        return ticketService.crear(dto);
    }

    @PostMapping("/asignar")
    public TicketDto asignar(@RequestBody TicketAsignarDto dto){
        return ticketService.asignar(dto);
    }

    @PostMapping("/cambiar-estado")
    public TicketDto cambiarEstado(@RequestBody TicketEstadoDto dto){
        return ticketService.cambiarEstado(dto);
    }

    @PostMapping("/cerrar")
    public TicketDto cerrar(@RequestBody TicketSolucionDto dto){
        return ticketService.cerrarConSolucion(dto);
    }

//    Ticket Comentario
    @PostMapping("/comentarios/listar")
    public List<TicketComentarioDto> listarComentarios(@RequestBody TicketComentarioCrearDto dto) {
        return ticketService.listarComentarios(dto.getTicketId());
    }

    @PostMapping("/comentarios/agregar")
    public TicketComentarioDto agregarComentario(@RequestBody TicketComentarioCrearDto dto) {
        return ticketService.agregarComentario(dto);
    }

//    Ticket Adjunto

    @PostMapping("/adjuntos/listar")
    public List<TicketAjuntoDto> listarAdjuntos(@PathVariable Long id) {
        return ticketService.listarAdjuntos(id);
    }
    @PostMapping("/adjuntos/registrar")
    public TicketAjuntoDto registrarAdjunto(@RequestBody TicketAdjuntoCrearDto dto) {
        return ticketService.registrarAdjunto(dto);
    }

    @PostMapping("/adjuntos/inactivar")
    public void inactivarAdjunto(@PathVariable Long id) {
        ticketService.inactivarAdjunto(id);
    }
//Ticket Historial

    @PostMapping("historial/listar")
    public List<TicketHistorialDto> listarHistorial (@RequestBody TicketDto dto){
        return ticketService.listarHistorial(dto.getId());
    }

}
