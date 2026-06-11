package com.ryot.helpdesk.service;

import com.ryot.helpdesk.dto.Auth.LoginRequestDto;
import com.ryot.helpdesk.dto.Auth.LoginResponseDto;
import com.ryot.helpdesk.dto.Usuario.UsuarioDto;
import com.ryot.helpdesk.entity.Usuario;
import com.ryot.helpdesk.mapper.UsuarioMapper;
import com.ryot.helpdesk.repository.UsuarioRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepo usuarioRepo;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto dto) {
        validarLogin(dto);

        Usuario usuario = usuarioRepo.findByEmailIgnoreCase(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales incorrectas"));

        if (!"ACTIVO".equals(usuario.getEstadoRegistro())) {
            throw new RuntimeException("El usuario está inactivo");
        }

        boolean passwordCorrecto = passwordEncoder.matches(
                dto.getPassword(),
                usuario.getPasswordHash()
        );

        if (!passwordCorrecto) {
            throw new RuntimeException("Credenciales incorrectas");
        }

        String token = jwtService.generarToken(usuario);
        UsuarioDto usuarioDto = usuarioMapper.toDto(usuario);

        return new LoginResponseDto(token, usuarioDto);
    }

    private void validarLogin(LoginRequestDto dto) {
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new RuntimeException("El email es obligatorio");
        }

        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new RuntimeException("La contraseña es obligatoria");
        }
    }
}