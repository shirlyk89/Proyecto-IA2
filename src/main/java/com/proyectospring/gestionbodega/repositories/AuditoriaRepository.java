package com.proyectospring.gestionbodega.repositories;

import com.proyectospring.gestionbodega.entities.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
    
    List<Auditoria> findByUsuario(String usuario);
    List<Auditoria> findByOperacion(String operacion);
}
