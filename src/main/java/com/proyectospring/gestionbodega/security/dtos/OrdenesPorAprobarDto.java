package com.proyectospring.gestionbodega.security.dtos;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class OrdenesPorAprobarDto {
    private long cantidad;
    private BigDecimal montoTotal;
}