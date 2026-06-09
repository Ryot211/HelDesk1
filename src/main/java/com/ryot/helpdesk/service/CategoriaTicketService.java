package com.ryot.helpdesk.service;

import com.ryot.helpdesk.dto.Categoria.CategoriaTicketDto;
import com.ryot.helpdesk.entity.CategoriaTicket;
import com.ryot.helpdesk.mapper.CategoriaTicketMapper;
import com.ryot.helpdesk.repository.CategoriaTicketRepo;
import com.ryot.helpdesk.utils.SisVars;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaTicketService {

    @Autowired
    private CategoriaTicketRepo categoriaTicketRepo;
    @Autowired
    private CategoriaTicketMapper categoriaTicketMapper;

    public List<CategoriaTicketDto> listarActivos() {
        List<CategoriaTicket> categorias =
                categoriaTicketRepo.findByEstadoRegistroOrderByIdAsc(SisVars.Activo);

        return categoriaTicketMapper.toDto(categorias);
    }

    public List<CategoriaTicketDto> listarTodos() {
        List<CategoriaTicket> categorias = categoriaTicketRepo.findAll();
        return categoriaTicketMapper.toDto(categorias);
    }

    public CategoriaTicketDto buscarPorId(Long id) {
        CategoriaTicket categoria = categoriaTicketRepo.findById(id).orElseThrow(() -> new RuntimeException("Categoria no encontrada"+id));
        return categoriaTicketMapper.toDto(categoria);
    }

    public CategoriaTicketDto guardar(CategoriaTicketDto dto) {
        if(dto.getNombre() == null|| dto.getNombre().isBlank()){
            throw new RuntimeException("Nombre requerido");
        }
        if(dto.getId() == null){
            return CreaNuevo(dto);
        }
        return ActualizarRegistro(dto);
    }
    private CategoriaTicketDto CreaNuevo(CategoriaTicketDto dto) {
        if(categoriaTicketRepo.existsByNombreIgnoreCase(dto.getNombre())){
            throw new RuntimeException("Ya existe una categoria con ese nombre :"+dto.getNombre());
        }
        CategoriaTicket entity = categoriaTicketMapper.toEntity(dto);
        entity.setId(null);

        if(entity.getEstadoRegistro()==null){
            entity.setEstadoRegistro(SisVars.Activo);
        }
        entity.setFechaCreacion(LocalDateTime.now());
        CategoriaTicket guardado = categoriaTicketRepo.save(entity);

        return categoriaTicketMapper.toDto(guardado);
    }

    private CategoriaTicketDto ActualizarRegistro(CategoriaTicketDto dto) {
        CategoriaTicket categoria = categoriaTicketRepo.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"+dto.getId()));

        if(categoriaTicketRepo.existsByNombreIgnoreCaseAndIdNot(dto.getNombre(),dto.getId())){
            throw new RuntimeException("Nombre existente"+dto.getNombre());
        }
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        if(dto.getEstadoRegistro()!=null){
            categoria.setEstadoRegistro(dto.getEstadoRegistro());
        }

        CategoriaTicket actualizado = categoriaTicketRepo.save(categoria);
        return categoriaTicketMapper.toDto(actualizado);
    }

    public void inactivar (Long id ){
        CategoriaTicket categoria = categoriaTicketRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"+id));
        categoria.setEstadoRegistro(SisVars.INACTIVO);
        categoriaTicketRepo.save(categoria);
    }

}
