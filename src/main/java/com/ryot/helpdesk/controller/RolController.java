package com.ryot.helpdesk.controller;


import com.ryot.helpdesk.dto.RolDto;
import com.ryot.helpdesk.service.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolController {

    private final RolService rolService;

    @PostMapping("/listar")
    public List<RolDto> listarActivos(){
        return rolService.listarActivos();
    }

    @PostMapping("/listar-todos")
    public List<RolDto> listarTodos(){
        return rolService.listarTodos();
    }

    @PostMapping("/buscar/{id}")
    public RolDto buscar(@PathVariable Long id){
        return rolService.buscarPorId(id);
    }

    @PostMapping("/guardar")
    public RolDto guardar(@RequestBody RolDto dto){
        return rolService.guardar(dto);
    }

    @PostMapping("/inactivar/{id}")
    public void inactivar(@PathVariable Long id){
        rolService.inactivar(id);
    }

}
