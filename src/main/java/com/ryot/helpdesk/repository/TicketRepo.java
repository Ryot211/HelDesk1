package com.ryot.helpdesk.repository;

import com.ryot.helpdesk.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepo extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByOrderByIdDesc();

    List<Ticket> findByEstadoOrderByIdDesc(String Estado);

    boolean existsByCodigo(String codigo);

}
