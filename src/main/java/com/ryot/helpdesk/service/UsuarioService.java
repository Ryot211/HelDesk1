package com.ryot.helpdesk.service;


import com.ryot.helpdesk.dto.UsuarioCrearDto;
import com.ryot.helpdesk.dto.UsuarioDto;
import com.ryot.helpdesk.dto.UsuarioPasswordDto;
import com.ryot.helpdesk.entity.Departamento;
import com.ryot.helpdesk.entity.Rol;
import com.ryot.helpdesk.entity.Usuario;
import com.ryot.helpdesk.mapper.UsuarioMapper;
import com.ryot.helpdesk.repository.DepartamentoRepo;
import com.ryot.helpdesk.repository.RolRepo;
import com.ryot.helpdesk.repository.UsuarioRepo;
import com.ryot.helpdesk.utils.SisVars;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    @Autowired
    private UsuarioRepo usuarioRepo;
    @Autowired
    private RolRepo rolRepo;
    @Autowired
    private DepartamentoRepo departamentoRepo;
    @Autowired
    private UsuarioMapper usuarioMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UsuarioDto> listarActivos() {
        List<Usuario> usuarios = usuarioRepo.findByEstadoRegistroOrderByIdAsc(SisVars.Activo);
        return usuarioMapper.toDto(usuarios);
    }

    public List<UsuarioDto> listarTodos(){
        List<Usuario> usuarios = usuarioRepo.findAll();
        return usuarioMapper.toDto(usuarios);
    }

    public UsuarioDto buscarPorId(Long id){
        Usuario usuario = usuarioRepo.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"+id));
        return usuarioMapper.toDto(usuario);
    }

    public UsuarioDto crear(UsuarioCrearDto dto) {
        validarCrear(dto);

        if (usuarioRepo.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con el email: " + dto.getEmail());
        }

        Rol rol = rolRepo.findById(dto.getRolId())
                .orElseThrow(() -> new RuntimeException("No existe el rol con id: " + dto.getRolId()));

        Departamento departamento = departamentoRepo.findById(dto.getDepartamentoId())
                .orElseThrow(() -> new RuntimeException("No existe el departamento con id: " + dto.getDepartamentoId()));

        Usuario usuario = new Usuario();
        usuario.setRol(rol);
        usuario.setDepartamento(departamento);
        usuario.setNombres(dto.getNombres());
        usuario.setApellidos(dto.getApellidos());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefono(dto.getTelefono());
        usuario.setEstadoRegistro(SisVars.Activo);

        String passwordHash = passwordEncoder.encode(dto.getPassword());
        usuario.setPasswordHash(passwordHash);

        Usuario guardado = usuarioRepo.save(usuario);
        return usuarioMapper.toDto(guardado);
    }

    public UsuarioDto actualizar(UsuarioDto dto) {
        validarActualizar(dto);

        Usuario usuario = usuarioRepo.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("No existe el usuario con id: " + dto.getId()));

        if (usuarioRepo.existsByEmailIgnoreCaseAndIdNot(dto.getEmail(), dto.getId())) {
            throw new RuntimeException("Ya existe otro usuario con el email: " + dto.getEmail());
        }

        Long rolId = dto.getRol().getId();
        Long departamentoId = dto.getDepartamento().getId();

        Rol rol = rolRepo.findById(rolId)
                .orElseThrow(() -> new RuntimeException("No existe el rol con id: " + rolId));

        Departamento departamento = departamentoRepo.findById(departamentoId)
                .orElseThrow(() -> new RuntimeException("No existe el departamento con id: " + departamentoId));

        usuario.setRol(rol);
        usuario.setDepartamento(departamento);
        usuario.setNombres(dto.getNombres());
        usuario.setApellidos(dto.getApellidos());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefono(dto.getTelefono());

        if (dto.getEstadoRegistro() != null) {
            usuario.setEstadoRegistro(dto.getEstadoRegistro());
        }

        Usuario actualizado = usuarioRepo.save(usuario);
        return usuarioMapper.toDto(actualizado);
    }

    public void inactivar(Long id) {
        Usuario usuario = usuarioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe el usuario con id: " + id));

        usuario.setEstadoRegistro(SisVars.INACTIVO);
        usuarioRepo.save(usuario);
    }

    public void cambiarPassword(UsuarioPasswordDto dto) {
        validarCambioPassword(dto);

        Usuario usuario = usuarioRepo.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("No existe el usuario con id: " + dto.getId()));

        boolean passwordActualCorrecta = passwordEncoder.matches(
                dto.getPasswordActual(),
                usuario.getPasswordHash()
        );

        if (!passwordActualCorrecta) {
            throw new RuntimeException("La contraseña actual no es correcta");
        }

        String nuevoHash = passwordEncoder.encode(dto.getPasswordNueva());
        usuario.setPasswordHash(nuevoHash);

        usuarioRepo.save(usuario);
    }
    private void validarCrear(UsuarioCrearDto dto) {
        if (dto.getRolId() == null) {
            throw new RuntimeException("El rol es obligatorio");
        }

        if (dto.getDepartamentoId() == null) {
            throw new RuntimeException("El departamento es obligatorio");
        }

        if (dto.getNombres() == null || dto.getNombres().isBlank()) {
            throw new RuntimeException("Los nombres son obligatorios");
        }

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new RuntimeException("El email es obligatorio");
        }

        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new RuntimeException("La contraseña es obligatoria");
        }
    }

    private void validarActualizar(UsuarioDto dto) {
        if (dto.getId() == null) {
            throw new RuntimeException("El id del usuario es obligatorio");
        }

        if (dto.getRol() == null || dto.getRol().getId() == null) {
            throw new RuntimeException("El rol es obligatorio");
        }

        if (dto.getDepartamento() == null || dto.getDepartamento().getId() == null) {
            throw new RuntimeException("El departamento es obligatorio");
        }

        if (dto.getNombres() == null || dto.getNombres().isBlank()) {
            throw new RuntimeException("Los nombres son obligatorios");
        }

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new RuntimeException("El email es obligatorio");
        }
    }

    private void validarCambioPassword(UsuarioPasswordDto dto) {
        if (dto.getId() == null) {
            throw new RuntimeException("El id del usuario es obligatorio");
        }

        if (dto.getPasswordActual() == null || dto.getPasswordActual().isBlank()) {
            throw new RuntimeException("La contraseña actual es obligatoria");
        }

        if (dto.getPasswordNueva() == null || dto.getPasswordNueva().isBlank()) {
            throw new RuntimeException("La nueva contraseña es obligatoria");
        }
    }
}
