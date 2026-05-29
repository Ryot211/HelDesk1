package com.ryot.helpdesk.repository;

import com.ryot.helpdesk.entity.TicketAdjunto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketAdjuntoRepo extends JpaRepository<TicketAdjunto, Long> {

    List<TicketAdjunto> findByTicketIdAndEstadoRegistroOrderByFechaCreacionDesc(Long ticketId, String estadoRegistro);

    List<TicketAdjunto> findByTicketIdOrderByFechaCreacionDesc(Long ticketId);
}
