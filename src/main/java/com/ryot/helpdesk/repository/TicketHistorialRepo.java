package com.ryot.helpdesk.repository;

import com.ryot.helpdesk.entity.TicketHistorial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketHistorialRepo extends JpaRepository<TicketHistorial,Long> {

    List<TicketHistorial> findByTicketIdOrderByFechaCreacionAsc(Long ticketId);

    List<TicketHistorial> findByTicketIdOrderByFechaCreacionDesc(Long ticketId);
}
