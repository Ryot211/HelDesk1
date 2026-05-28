package com.ryot.helpdesk.controller;


import com.ryot.helpdesk.dto.Usuario.UsuarioCrearDto;
import com.ryot.helpdesk.dto.Usuario.UsuarioDto;
import com.ryot.helpdesk.dto.Usuario.UsuarioPasswordDto;
import com.ryot.helpdesk.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/listar")
    public List<UsuarioDto> listarActivos() {
        return usuarioService.listarActivos();
    }

    @PostMapping("/listar-todos")
    public List<UsuarioDto> listarTodos() {
        return usuarioService.listarTodos();
    }

    @PostMapping("/buscar/{id}")
    public UsuarioDto buscar(@PathVariable Long id) {
        return usuarioService.buscarPorId(id);
    }
    @PostMapping("/crear")
    public UsuarioDto crear(@RequestBody UsuarioCrearDto dto) {
        return usuarioService.crear(dto);
    }

    @PostMapping("/actualizar")
    public UsuarioDto actualizar(@RequestBody UsuarioDto dto) {
        return usuarioService.actualizar(dto);
    }

    @PostMapping("/inactivar")
    public void inactivar(@RequestBody UsuarioDto dto) {
        usuarioService.inactivar(dto.getId());
    }
    @PostMapping("/cambiar-password")
    public void cambiarPassword(@RequestBody UsuarioPasswordDto dto) {
        usuarioService.cambiarPassword(dto);
    }


}
