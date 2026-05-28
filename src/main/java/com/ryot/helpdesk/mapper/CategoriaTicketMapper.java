package com.ryot.helpdesk.mapper;


import com.ryot.helpdesk.dto.Categoria.CategoriaTicketDto;
import com.ryot.helpdesk.entity.CategoriaTicket;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper (componentModel = "spring" )
public interface CategoriaTicketMapper {

    CategoriaTicket toEntity(CategoriaTicketDto dto);
    CategoriaTicketDto toDto(CategoriaTicket entity);

    List<CategoriaTicket> toEntity(List<CategoriaTicketDto> dto);
    List<CategoriaTicketDto> toDto(List<CategoriaTicket> entity);
}
