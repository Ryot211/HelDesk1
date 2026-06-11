package com.ryot.helpdesk.dto.Auth;

import com.ryot.helpdesk.dto.Usuario.UsuarioDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDto {

    private String token;
    private UsuarioDto usuario;
}