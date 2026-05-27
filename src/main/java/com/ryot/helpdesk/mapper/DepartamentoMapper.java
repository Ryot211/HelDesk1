package com.ryot.helpdesk.mapper;

import com.ryot.helpdesk.dto.DepartamentoDto;
import com.ryot.helpdesk.entity.Departamento;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper (componentModel = "spring")
public interface DepartamentoMapper {

    Departamento toEntity(DepartamentoDto dto);
    DepartamentoDto toDto(Departamento entity);
    List<Departamento> toEntity(List<DepartamentoDto> dto);
    List<DepartamentoDto> toDto(List<Departamento> entity);

}
