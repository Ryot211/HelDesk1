package com.ryot.helpdesk.dto.Usuario;

import com.ryot.helpdesk.dto.Departamento.DepartamentoDto;
import com.ryot.helpdesk.dto.Rol.RolDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UsuarioDto {

    private Long id;

    private RolDto rol;
    private DepartamentoDto departamento;

    private String nombres;
    private String apellidos;
    private String email;
    private String telefono;
    private String estadoRegistro;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}