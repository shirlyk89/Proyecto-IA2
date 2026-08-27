package com.proyectospring.gestionbodega.services;

import java.util.List;

import com.proyectospring.gestionbodega.security.dtos.KpiDto;
import com.proyectospring.gestionbodega.security.dtos.OcupacionBodegaDto;
import com.proyectospring.gestionbodega.security.dtos.ProductoRiesgoDto;
import com.proyectospring.gestionbodega.security.dtos.StockProductoDto;

public interface PanelService {

    KpiDto calcularKpis();

    List<ProductoRiesgoDto> listarProductosEnRiesgo();

    List<OcupacionBodegaDto> listarBodegasCriticas();

    StockProductoDto obtenerStockProducto(Long productoId);
}