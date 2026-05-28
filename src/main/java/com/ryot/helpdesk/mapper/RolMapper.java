package com.ryot.helpdesk.mapper;

import com.ryot.helpdesk.dto.Rol.RolDto;
import com.ryot.helpdesk.entity.Rol;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper (componentModel = "spring")
public interface RolMapper {

    Rol toEntity(RolDto rolDto);
    RolDto toDto(Rol rol);

    List<Rol> toEntity(List<RolDto> dto);
    List<RolDto> toDto(List<Rol> entity);

}
