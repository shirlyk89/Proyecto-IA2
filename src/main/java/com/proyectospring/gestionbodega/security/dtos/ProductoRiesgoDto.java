package com.proyectospring.gestionbodega.security.dtos;

import java.math.BigDecimal;
import com.proyectospring.gestionbodega.entities.EstadoCobertura;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ProductoRiesgoDto {
    private Long productoId;
    private String nombreProducto;
    private Long proveedorId;
    private int stockTotal;
    private BigDecimal consumoDiarioPromedio;
    private BigDecimal puntoReorden;
    private BigDecimal diasCobertura;
    private EstadoCobertura estadoCobertura;
    private Long bodegaDestinoId;
}