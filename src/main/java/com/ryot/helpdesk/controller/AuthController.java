package com.ryot.helpdesk.controller;

import com.ryot.helpdesk.dto.Auth.LoginRequestDto;
import com.ryot.helpdesk.dto.Auth.LoginResponseDto;
import com.ryot.helpdesk.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto dto) {
        return authService.login(dto);
    }
}