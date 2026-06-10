package com.ryot.helpdesk.service;


import com.ryot.helpdesk.dto.Auth.LoginRequestDto;
import com.ryot.helpdesk.dto.Usuario.UsuarioDto;
import com.ryot.helpdesk.entity.Usuario;
import com.ryot.helpdesk.mapper.UsuarioMapper;
import com.ryot.helpdesk.repository.UsuarioRepo;
import com.ryot.helpdesk.utils.SisVars;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Autowired
    private UsuarioRepo usuarioRepo;
    @Autowired
    private UsuarioMapper usuarioMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Transactional(readOnly = true)
    public UsuarioDto login (LoginRequestDto dto){
        if(dto.getEmail() == null ){
            throw new RuntimeException("El email es nescesario");
        }
        if(dto.getPassword() == null ){
            throw new RuntimeException("El password es nescesario");
        }

        Usuario usuario = usuarioRepo.findByEmailIgnoreCase(dto.getEmail()).
                        orElseThrow(() -> new RuntimeException("Credenciales Incorrectas"));

        if(!SisVars.Activo.equals(usuario.getEstadoRegistro())){
            throw new RuntimeException("El usuario esta inactivo");
        }
        boolean passwordCorrecto = passwordEncoder.matches(
                dto.getPassword(),
                usuario.getPasswordHash());

        if(!passwordCorrecto){
            throw  new RuntimeException(("Credenciales inconrrectas"));
        }

        return usuarioMapper.toDto(usuario);

    }


}
