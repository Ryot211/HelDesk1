package com.ryot.helpdesk.repository;

import com.ryot.helpdesk.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepo extends JpaRepository<Usuario,Long> {
    List<Usuario> findByEstadoRegistroOrderByIdAsc(String estadoRegistro);

    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email,Long id);

}
