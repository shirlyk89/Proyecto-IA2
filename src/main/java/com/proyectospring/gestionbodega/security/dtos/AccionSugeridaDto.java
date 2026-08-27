package com.proyectospring.gestionbodega.security.dtos;

import com.proyectospring.gestionbodega.entities.TipoAccion;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AccionSugeridaDto {
    private TipoAccion tipo;
    private String descripcion;
    private Long ordenId;
    private Long productoId;
    private Long bodegaId;
}