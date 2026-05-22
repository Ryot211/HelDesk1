package com.ryot.helpdesk.repository;

import com.ryot.helpdesk.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RolRepo extends JpaRepository<Rol, Long> {

    List<Rol> findByEstadoRegistroOrderByIdAsc(String estadoRegistro);

    Optional<Rol> findByCodigo(String codigo);
}