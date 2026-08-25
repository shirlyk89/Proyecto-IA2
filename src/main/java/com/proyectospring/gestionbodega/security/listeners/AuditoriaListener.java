package com.proyectospring.gestionbodega.security.listeners;

import com.proyectospring.gestionbodega.entities.Auditoria;
import com.proyectospring.gestionbodega.entities.Bodega;
import com.proyectospring.gestionbodega.entities.Movimiento;
import com.proyectospring.gestionbodega.entities.Producto;
import com.proyectospring.gestionbodega.repositories.AuditoriaRepository;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaListener {

    // Usamos @Lazy para asegurar que Hibernate no tenga problemas de inicialización 
    // al inyectar un repositorio de Spring dentro de un EntityListener.
    @Autowired
    @Lazy
    private AuditoriaRepository auditoriaRepository;

    @PostPersist
    public void onPostPersist(Object entity) {
        registrarAuditoria(entity, "INSERT");
    }

    @PostUpdate
    public void onPostUpdate(Object entity) {
        registrarAuditoria(entity, "UPDATE");
    }

    @PostRemove
    public void onPostRemove(Object entity) {
        registrarAuditoria(entity, "DELETE");
    }

    private void registrarAuditoria(Object entity, String operacion) {
        // Solo auditamos las entidades de inventario, ignoramos si se cruza otra entidad
        if (entity instanceof Bodega || entity instanceof Producto || entity instanceof Movimiento) {
            
            String entidadNombre = entity.getClass().getSimpleName();
            String idEntidad = obtenerIdGenerico(entity);
            String usuario = getCurrentUser();

            Auditoria auditoria = new Auditoria(entidadNombre, idEntidad, operacion, usuario);
            
            auditoriaRepository.save(auditoria);
        }
    }

    private String obtenerIdGenerico(Object entity) {
        // Hacemos un "cast" seguro a las clases de Persona A para extraer su ID
        if (entity instanceof Bodega) {
            return String.valueOf(((Bodega) entity).getId());
        } else if (entity instanceof Producto) {
            return String.valueOf(((Producto) entity).getId());
        } else if (entity instanceof Movimiento) {
            return String.valueOf(((Movimiento) entity).getId());
        }
        return "Desconocido";
    }

    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            return auth.getName();
        }
        return "SISTEMA"; // Por si se crea un registro desde consola o al iniciar la app
    }
}