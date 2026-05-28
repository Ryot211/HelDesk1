package com.ryot.helpdesk.dto.Usuario;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioPasswordDto {
    private Long id;
    private String passwordActual;
    private String passwordNueva;

}
