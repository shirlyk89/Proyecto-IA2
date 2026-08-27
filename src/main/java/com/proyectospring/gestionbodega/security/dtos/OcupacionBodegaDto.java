package com.proyectospring.gestionbodega.security.dtos;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class OcupacionBodegaDto {
    private Long bodegaId;
    private String nombre;
    private BigDecimal porcentaje;
}