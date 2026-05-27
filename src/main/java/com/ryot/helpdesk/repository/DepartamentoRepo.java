package com.ryot.helpdesk.repository;

import com.ryot.helpdesk.entity.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartamentoRepo extends JpaRepository<Departamento, Long> {


    List<Departamento> findByEstadoRegistroOrderByNombreAsc(String estadoRegistro);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

}
