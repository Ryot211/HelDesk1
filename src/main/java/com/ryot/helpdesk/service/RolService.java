package com.ryot.helpdesk.service;

import com.ryot.helpdesk.dto.Rol.RolDto;
import com.ryot.helpdesk.entity.Rol;
import com.ryot.helpdesk.mapper.RolMapper;
import com.ryot.helpdesk.repository.RolRepo;
import com.ryot.helpdesk.utils.SisVars;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepo rolRepo;
    private final RolMapper rolMapper;

    public List<RolDto> listarActivos(){
        List<Rol> roles = rolRepo.findByEstadoRegistroOrderByIdAsc(SisVars.Activo);
        return rolMapper.toDto(roles);
    }

    public List<RolDto> listarTodos(){
        List<Rol> roles = rolRepo.findAll();
        return rolMapper.toDto(roles);
    }

    public RolDto buscarPorId(Long id){
        Rol rol = rolRepo.findById(id).orElseThrow(() -> new RuntimeException("No existe el rol con el id:" +id));

        return rolMapper.toDto(rol);
    }

    public RolDto guardar(RolDto dto) {
        validarDatosObligatorios(dto);

        if (dto.getId() == null) {
            return crearNuevo(dto);
        }

        return actualizarExistente(dto);
    }

    private RolDto crearNuevo(RolDto dto) {
        rolRepo.findByCodigo(dto.getCodigo())
                .ifPresent(rol -> {
                    throw new RuntimeException("Ya existe un rol con el código: " + dto.getCodigo());
                });

        Rol entity = rolMapper.toEntity(dto);
        entity.setId(null);// esto esta raro como lo toma la ia

        if (entity.getEstadoRegistro() == null) {
            entity.setEstadoRegistro("ACTIVO");
        }

        Rol guardado = rolRepo.save(entity);
        return rolMapper.toDto(guardado);
    }

    private RolDto actualizarExistente(RolDto dto) {
        Rol rol = rolRepo.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("No existe el rol con el id: " + dto.getId()));
        rol.setCodigo(dto.getCodigo());
        rol.setNombre(dto.getNombre());
        rol.setDescripcion(dto.getDescripcion());

        if (dto.getEstadoRegistro() != null) {
            rol.setEstadoRegistro(dto.getEstadoRegistro());
        }

        Rol actualizado = rolRepo.save(rol);
        return rolMapper.toDto(actualizado);
    }

    public void inactivar(Long id) {
        Rol rol = rolRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe el rol con id: " + id));

        rol.setEstadoRegistro("INACTIVO");
        rolRepo.save(rol);
    }

    private void validarDatosObligatorios(RolDto dto) {
        if (dto.getCodigo() == null || dto.getCodigo().isBlank()) {
            throw new RuntimeException("El código del rol es obligatorio");
        }

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new RuntimeException("El nombre del rol es obligatorio");
        }
    }



    }








