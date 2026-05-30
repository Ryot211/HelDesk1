package com.ryot.helpdesk.mapper;

import com.ryot.helpdesk.dto.Ticket.TicketHistorialDto;
import com.ryot.helpdesk.entity.TicketHistorial;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper (componentModel  ="spring", uses={
        TicketMapper.class,
        UsuarioMapper.class
        })
public interface TicketHistorialMapper {
    TicketHistorialDto toDto (TicketHistorial entity);
    List<TicketHistorialDto> toDto (List<TicketHistorial> entity);
}
