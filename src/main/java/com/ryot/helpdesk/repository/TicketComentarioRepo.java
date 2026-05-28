package com.ryot.helpdesk.repository;

import com.ryot.helpdesk.entity.TicketComentario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketComentarioRepo extends JpaRepository<TicketComentario,Long> {
    List<TicketComentario> findByTicketIdOrderByFechaCreacionAsc(Long ticketId);
}
