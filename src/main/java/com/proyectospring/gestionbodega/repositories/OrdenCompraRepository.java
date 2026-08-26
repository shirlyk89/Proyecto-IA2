package com.proyectospring.gestionbodega.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyectospring.gestionbodega.entities.EstadoOrden;
import com.proyectospring.gestionbodega.entities.OrdenCompra;

public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {

    List<OrdenCompra> findByEstado(EstadoOrden estado);
}