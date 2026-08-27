package com.proyectospring.gestionbodega.services;

import com.proyectospring.gestionbodega.entities.ResumenPanel;
import com.proyectospring.gestionbodega.security.dtos.ResumenPanelRequest;

public interface ResumenPanelService {

    /** Valida y publica el resumen. Si ya existe uno para esa fecha, lo reemplaza. */
    ResumenPanel publicar(ResumenPanelRequest request, String autor);

    /** Devuelve el resumen de la fecha más reciente. */
    ResumenPanel obtenerUltimo();
}
