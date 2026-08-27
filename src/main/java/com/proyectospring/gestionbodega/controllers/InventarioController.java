package com.proyectospring.gestionbodega.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyectospring.gestionbodega.security.dtos.KpiDto;
import com.proyectospring.gestionbodega.security.dtos.OcupacionBodegaDto;
import com.proyectospring.gestionbodega.security.dtos.ProductoRiesgoDto;
import com.proyectospring.gestionbodega.security.dtos.StockProductoDto;
import com.proyectospring.gestionbodega.services.PanelService;

@RestController
@RequestMapping("/api")
public class InventarioController {

    private final PanelService panelService;

    public InventarioController(PanelService panelService) {
        this.panelService = panelService;
    }

    @GetMapping("/kpis")
    public KpiDto kpis() {
        return panelService.calcularKpis();
    }

    @GetMapping("/productos/{id}/stock")
    public StockProductoDto stock(@PathVariable Long id) {
        return panelService.obtenerStockProducto(id);
    }

    @GetMapping("/productos/riesgo")
    public List<ProductoRiesgoDto> productosEnRiesgo() {
        return panelService.listarProductosEnRiesgo();
    }

    @GetMapping("/bodegas/criticas")
    public List<OcupacionBodegaDto> bodegasCriticas() {
        return panelService.listarBodegasCriticas();
    }
}