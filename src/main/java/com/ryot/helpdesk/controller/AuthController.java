package com.ryot.helpdesk.controller;


import com.ryot.helpdesk.dto.Auth.LoginRequestDto;
import com.ryot.helpdesk.dto.Usuario.UsuarioDto;
import com.ryot.helpdesk.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public UsuarioDto login(@RequestBody LoginRequestDto dto){
        return authService.login(dto);
    }

}
