package com.proyectospring.gestionbodega.security.dtos;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class StockProductoDto {
    private int stockTotal;
    private Map<Long, Integer> stockPorBodega;
}