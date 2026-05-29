package com.ryot.helpdesk.mapper;


import com.ryot.helpdesk.dto.Ticket.TicketAdjunto.TicketAdjuntoCrearDto;
import com.ryot.helpdesk.dto.Ticket.TicketAdjunto.TicketAjuntoDto;
import com.ryot.helpdesk.entity.TicketAdjunto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper (componentModel = "spring", uses ={
        TicketMapper.class,
        UsuarioMapper.class
})
public interface TicketAdjuntoMapper {

    TicketAjuntoDto toDto(TicketAdjunto entity);
    List<TicketAjuntoDto> toDto(List<TicketAdjunto> entity);

}
