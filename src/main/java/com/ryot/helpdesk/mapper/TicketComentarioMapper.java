package com.ryot.helpdesk.mapper;

import com.ryot.helpdesk.dto.Ticket.TicketComentario.TicketComentarioDto;
import com.ryot.helpdesk.entity.TicketComentario;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses={
        TicketMapper.class,
        UsuarioMapper.class
})
public interface TicketComentarioMapper {
    TicketComentarioDto toDto(TicketComentario entity);

    List<TicketComentarioDto> toDto(List<TicketComentario> entity);
}
