package com.proyectospring.gestionbodega.security.dtos;

import com.proyectospring.gestionbodega.entities.EstadoOrden;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CambiarEstadoRequest {
    private EstadoOrden estado;
}