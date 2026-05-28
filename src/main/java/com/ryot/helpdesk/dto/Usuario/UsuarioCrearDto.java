package com.ryot.helpdesk.dto.Usuario;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioCrearDto {

    private Long rolId;
    private Long departamentoId;

    private String nombres;
    private String apellidos;
    private String email;
    private String password;
    private String telefono;
}