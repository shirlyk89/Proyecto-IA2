package com.proyectospring.gestionbodega.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyectospring.gestionbodega.entities.Usuario;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Spring Data JPA hace la magia aquí. Lo necesitaremos para el login.
    Optional<Usuario> findByUsername(String username);
    
    boolean existsByUsername(String username);
}