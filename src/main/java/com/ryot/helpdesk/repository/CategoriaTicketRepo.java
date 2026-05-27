package com.ryot.helpdesk.repository;

import com.ryot.helpdesk.entity.CategoriaTicket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaTicketRepo extends JpaRepository<CategoriaTicket,Long> {
    List<CategoriaTicket> findByEstadoRegistroOrderByIdAsc(String estadoRegistro);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id );


}
