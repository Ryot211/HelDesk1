package com.ryot.helpdesk.mapper;


import com.ryot.helpdesk.dto.UsuarioDto;
import com.ryot.helpdesk.entity.Usuario;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper (componentModel = "spring", uses = {RolMapper.class, DepartamentoMapper.class})
public interface UsuarioMapper {
    UsuarioDto toDto(Usuario entity);

    List<UsuarioDto> toDto(List<Usuario> entity);

}
