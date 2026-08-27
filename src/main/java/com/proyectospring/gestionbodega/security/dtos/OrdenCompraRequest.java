package com.proyectospring.gestionbodega.security.dtos;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrdenCompraRequest {
    private Long productoId;
    private Long proveedorId;
    private Long bodegaDestinoId;
    private Integer cantidad;
    private BigDecimal precioUnitario;
}