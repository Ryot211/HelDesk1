package com.ryot.helpdesk.controller;

import com.ryot.helpdesk.dto.Categoria.CategoriaTicketDto;
import com.ryot.helpdesk.service.CategoriaTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias-ticket")
@RequiredArgsConstructor
public class CategoriaTickerController {

    @Autowired
    private CategoriaTicketService categoriaTicketService;

    @PostMapping("/listar")
    public List<CategoriaTicketDto> listarActivos(){
        return  categoriaTicketService.listarActivos();
    }

    @PostMapping ("/listar-todos")
    public List<CategoriaTicketDto> listarTodos(){
        return  categoriaTicketService.listarTodos();
    }

    @PostMapping("/buscar/{id}")
    public CategoriaTicketDto buscar(@PathVariable Long id){
        return  categoriaTicketService.buscarPorId(id);
    }

    @PostMapping("/guardar")
    public CategoriaTicketDto guardar(@RequestBody CategoriaTicketDto dto){
        return  categoriaTicketService.guardar(dto);
    }

    @PostMapping("/inactivar")
    public void inactivar(@RequestBody CategoriaTicketDto dto){
        categoriaTicketService.inactivar(dto.getId());
    }

}
