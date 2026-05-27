package com.ryot.helpdesk.controller;

import com.ryot.helpdesk.dto.DepartamentoDto;
import com.ryot.helpdesk.service.DepartamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departamentos")
@RequiredArgsConstructor

public class DepartamentoController {
    @Autowired
    private DepartamentoService departamentoService;

    @PostMapping("/Listar")
    public List<DepartamentoDto> listarActivos(){
        return departamentoService.listarActivos();
    }

    @PostMapping("/Listar-todos")
    public List<DepartamentoDto> listarTodos(){
        return departamentoService.listarTodos();
    }

    @PostMapping("/buscar/{id}")
    public DepartamentoDto buscarPorId(@PathVariable Long id){
        return departamentoService.buscarporID(id);
    }

    @PostMapping("/guardar")
    public DepartamentoDto guardar(@RequestBody DepartamentoDto dto){
        return departamentoService.guardar(dto);
    }

    @PostMapping("/inactivar/{id}")
    public void inactivar(@PathVariable Long id){
        departamentoService.inactivar(id);
    }

}
