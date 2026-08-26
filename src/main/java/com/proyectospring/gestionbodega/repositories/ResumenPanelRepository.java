package com.proyectospring.gestionbodega.repositories;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyectospring.gestionbodega.entities.ResumenPanel;

public interface ResumenPanelRepository extends JpaRepository<ResumenPanel, Long> {

    Optional<ResumenPanel> findByFecha(LocalDate fecha);

    Optional<ResumenPanel> findTopByOrderByFechaDesc();
}