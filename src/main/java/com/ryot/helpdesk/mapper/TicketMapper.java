package com.ryot.helpdesk.mapper;

import com.ryot.helpdesk.dto.Ticket.TicketDto;
import com.ryot.helpdesk.entity.Ticket;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring",uses={
            CategoriaTicketMapper.class,
            DepartamentoMapper.class,
        UsuarioMapper.class
})
public interface TicketMapper {
    TicketDto toDto(Ticket entity);

    List<TicketDto> toDtos(List<Ticket> entity);
}
