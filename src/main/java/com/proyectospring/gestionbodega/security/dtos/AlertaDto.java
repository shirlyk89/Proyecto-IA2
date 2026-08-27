package com.proyectospring.gestionbodega.security.dtos;

import com.proyectospring.gestionbodega.entities.Severidad;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AlertaDto {
    private Severidad severidad;
    private String titulo;
    private String detalle;
    private Long productoId;
    private Long ordenId;
    private Long bodegaId;
}