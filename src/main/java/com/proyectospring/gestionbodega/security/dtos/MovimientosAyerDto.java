package com.proyectospring.gestionbodega.security.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class MovimientosAyerDto {
    private long entrada;
    private long salida;
    private long transferencia;
}