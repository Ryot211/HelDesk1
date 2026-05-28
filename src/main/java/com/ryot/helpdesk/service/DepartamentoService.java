package com.ryot.helpdesk.service;


import com.ryot.helpdesk.dto.Departamento.DepartamentoDto;
import com.ryot.helpdesk.entity.Departamento;
import com.ryot.helpdesk.mapper.DepartamentoMapper;
import com.ryot.helpdesk.repository.DepartamentoRepo;
import com.ryot.helpdesk.utils.SisVars;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartamentoService {

    private final DepartamentoRepo departamentoRepo;
    private final DepartamentoMapper departamentoMapper;

    public List<DepartamentoDto> listarActivos(){
        List<Departamento> departamentos =
                    departamentoRepo.findByEstadoRegistroOrderByNombreAsc(SisVars.Activo);

        return departamentoMapper.toDto(departamentos);
    }


    public List<DepartamentoDto>  listarTodos() {
        List<Departamento> departamentos = departamentoRepo.findAll();
        return departamentoMapper.toDto(departamentos);
    }

    public DepartamentoDto buscarporID(Long id) {
        Departamento departamento = departamentoRepo.findById(id).orElseThrow(() -> new RuntimeException("No existe el departamento con el id:" +id));
        return  departamentoMapper.toDto(departamento);
    }
    public DepartamentoDto guardar(DepartamentoDto dto){
        validatosObligatorios(dto);

        if(dto.getId()==null){
            return  crearNuevo(dto);
        }
        return actualizarExistente(dto);
        
    }

    private DepartamentoDto crearNuevo(DepartamentoDto dto) {
        if(departamentoRepo.existsByNombreIgnoreCase(dto.getNombre())){
            throw new RuntimeException("Departamento ya existe un departamento con este nombre" + dto.getNombre() );
        }

        Departamento entity = departamentoMapper.toEntity(dto);
        entity.setId(null);

        if(entity.getEstadoRegistro()==null){
            entity.setEstadoRegistro(SisVars.Activo);
        }

        Departamento guardad = departamentoRepo.save(entity);
        return departamentoMapper.toDto(guardad);
    }
    
    private DepartamentoDto actualizarExistente(DepartamentoDto dto){
        Departamento depart = departamentoRepo.findById(dto.getId())
                .orElseThrow(()-> new RuntimeException("No existe el departamento con el id: "+ dto.getId()));
        if(departamentoRepo.existsByNombreIgnoreCaseAndIdNot(dto.getNombre(), dto.getId())){
            throw new RuntimeException("Ya existe un departamento con el nombre "+ dto.getNombre());
        }
        depart.setNombre(dto.getNombre());
        depart.setDescripcion(dto.getDescripcion());

        if(dto.getEstadoRegistro()!=null){
            depart.setEstadoRegistro(dto.getEstadoRegistro());
        }

        Departamento actualizado = departamentoRepo.save(depart);
        return departamentoMapper.toDto(actualizado);

    }

    public void inactivar(Long id){
        Departamento departamento = departamentoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe el departamento con el id: "+ id));
        departamento.setEstadoRegistro(SisVars.INACTIVO);
        departamentoRepo.save(departamento);
    }

    private void validatosObligatorios (DepartamentoDto dto){
        if(dto.getNombre() == null || dto.getNombre().isBlank()){
            throw new RuntimeException("El nombre del departamento es obligatorio");
        }
    }


}
