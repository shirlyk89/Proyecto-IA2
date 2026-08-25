package com.proyectospring.gestionbodega.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyectospring.gestionbodega.entities.Bodega;

public interface BodegaRepository extends JpaRepository<Bodega, Long> {

    
}