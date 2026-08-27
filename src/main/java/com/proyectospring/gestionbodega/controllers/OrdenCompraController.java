package com.proyectospring.gestionbodega.controllers;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyectospring.gestionbodega.security.dtos.CambiarEstadoRequest;
import com.proyectospring.gestionbodega.security.dtos.OrdenCompraRequest;
import com.proyectospring.gestionbodega.entities.Bodega;
import com.proyectospring.gestionbodega.entities.EstadoOrden;
import com.proyectospring.gestionbodega.entities.OrdenCompra;
import com.proyectospring.gestionbodega.entities.Producto;
import com.proyectospring.gestionbodega.entities.Proveedor;
import com.proyectospring.gestionbodega.exceptions.RecursoNoEncontradoException;
import com.proyectospring.gestionbodega.repositories.BodegaRepository;
import com.proyectospring.gestionbodega.repositories.OrdenCompraRepository;
import com.proyectospring.gestionbodega.repositories.ProductoRepository;
import com.proyectospring.gestionbodega.repositories.ProveedorRepository;
import com.proyectospring.gestionbodega.services.OrdenCompraService;
import com.proyectospring.gestionbodega.services.PdfService;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenCompraController {

    private final OrdenCompraService ordenCompraService;
    private final OrdenCompraRepository ordenCompraRepository;
    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;
    private final BodegaRepository bodegaRepository;

       private final PdfService pdfService;

    public OrdenCompraController(OrdenCompraService ordenCompraService,
                                 OrdenCompraRepository ordenCompraRepository,
                                 ProductoRepository productoRepository,
                                 ProveedorRepository proveedorRepository,
                                 BodegaRepository bodegaRepository,
                                 PdfService pdfService) {
        this.ordenCompraService = ordenCompraService;
        this.ordenCompraRepository = ordenCompraRepository;
        this.productoRepository = productoRepository;
        this.proveedorRepository = proveedorRepository;
        this.bodegaRepository = bodegaRepository;
        this.pdfService = pdfService;
    }

    @GetMapping
    public List<OrdenCompra> getAll(@RequestParam(required = false) EstadoOrden estado) {
        if (estado != null) {
            return ordenCompraRepository.findByEstado(estado);
        }
        return ordenCompraRepository.findAll();
    }

    @GetMapping("/{id}")
    public OrdenCompra getById(@PathVariable Long id) {
        return ordenCompraRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada con el ID: " + id));
    }

    @PostMapping
    public OrdenCompra crear(@RequestBody OrdenCompraRequest request, Authentication authentication) {
        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producto no encontrado con el ID: " + request.getProductoId()));

        Proveedor proveedor = proveedorRepository.findById(request.getProveedorId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Proveedor no encontrado con el ID: " + request.getProveedorId()));

        Bodega bodega = bodegaRepository.findById(request.getBodegaDestinoId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Bodega no encontrada con el ID: " + request.getBodegaDestinoId()));

        OrdenCompra orden = new OrdenCompra();
        orden.setProducto(producto);
        orden.setProveedor(proveedor);
        orden.setBodegaDestino(bodega);
        orden.setCantidad(request.getCantidad());
        orden.setPrecioUnitario(request.getPrecioUnitario());
        orden.setCreadoPor(authentication.getName());

        return ordenCompraService.crearBorrador(orden);
    }

    @PatchMapping("/{id}/estado")
    public OrdenCompra cambiarEstado(@PathVariable Long id, @RequestBody CambiarEstadoRequest request) {
        return ordenCompraService.cambiarEstado(id, request.getEstado());
    }

        @PostMapping("/{id}/pdf")
    public java.util.Map<String, Object> generarPdf(@PathVariable Long id) {
        OrdenCompra orden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada con el ID: " + id));

        byte[] pdf = pdfService.generarPdf(orden);
        orden.setPdfDocumento(pdf);
        orden.setPdfFechaGeneracion(java.time.LocalDateTime.now());
        ordenCompraRepository.save(orden);

        return java.util.Map.of(
                "mensaje", "PDF generado correctamente",
                "ordenId", orden.getId(),
                "pdfFechaGeneracion", orden.getPdfFechaGeneracion()
        );
    }

    @GetMapping("/{id}/pdf")
    public org.springframework.http.ResponseEntity<byte[]> obtenerPdf(@PathVariable Long id) {
        OrdenCompra orden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada con el ID: " + id));

        if (orden.getPdfDocumento() == null) {
            throw new RecursoNoEncontradoException(
                    "Aún no se ha generado el PDF para esta orden. Genera uno primero con POST /ordenes/" + id + "/pdf");
        }

        return org.springframework.http.ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .header("Content-Disposition", "inline; filename=orden-" + id + ".pdf")
                .body(orden.getPdfDocumento());
    }

    
}