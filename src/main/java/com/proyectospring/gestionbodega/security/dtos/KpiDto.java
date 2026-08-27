package com.proyectospring.gestionbodega.security.dtos;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class KpiDto {
    private OffsetDateTime calculadoEn;
    private List<OcupacionBodegaDto> ocupacionPorBodega;
    private long productosEnQuiebre;
    private long productosEnRiesgo;
    private OrdenesPorAprobarDto ordenesPorAprobar;
    private MovimientosAyerDto movimientosAyer;
}