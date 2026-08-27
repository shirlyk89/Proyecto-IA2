package com.proyectospring.gestionbodega.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyectospring.gestionbodega.entities.EstadoOrden;
import com.proyectospring.gestionbodega.entities.Movimiento;
import com.proyectospring.gestionbodega.entities.OrdenCompra;
import com.proyectospring.gestionbodega.entities.TipoMovimiento;
import com.proyectospring.gestionbodega.exceptions.TransicionInvalidaException;
import com.proyectospring.gestionbodega.repositories.MovimientoRepository;
import com.proyectospring.gestionbodega.repositories.OrdenCompraRepository;

@Service
public class OrdenCompraServiceImpl implements OrdenCompraService {

    private final OrdenCompraRepository ordenCompraRepository;
    private final MovimientoRepository movimientoRepository;

    public OrdenCompraServiceImpl(OrdenCompraRepository ordenCompraRepository,
                                   MovimientoRepository movimientoRepository) {
        this.ordenCompraRepository = ordenCompraRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @Override
    @Transactional
    public OrdenCompra crearBorrador(OrdenCompra orden) {
        if (orden.getCantidad() == null || orden.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad de la orden debe ser mayor que 0");
        }
        if (orden.getBodegaDestino() == null) {
            throw new IllegalArgumentException("La bodega destino es obligatoria");
        }

        orden.setTotal(orden.getPrecioUnitario().multiply(new java.math.BigDecimal(orden.getCantidad())));
        orden.setEstado(EstadoOrden.BORRADOR);
        orden.setFechaCreacion(LocalDateTime.now());
        // El PDF (si existía) queda invalidado porque es una orden nueva
        orden.setPdfDocumento(null);
        orden.setPdfFechaGeneracion(null);

        return ordenCompraRepository.save(orden);
    }

    @Override
    @Transactional
    public OrdenCompra cambiarEstado(Long ordenId, EstadoOrden nuevoEstado) {
        OrdenCompra orden = ordenCompraRepository.findById(ordenId)
                .orElseThrow(() -> new com.proyectospring.gestionbodega.exceptions.RecursoNoEncontradoException("Orden no encontrada con el ID: " + ordenId));

        validarTransicion(orden.getEstado(), nuevoEstado);

        if (orden.getEstado() == EstadoOrden.APROBADA && nuevoEstado == EstadoOrden.RECIBIDA) {
            Movimiento entrada = new Movimiento();
            entrada.setTipo(TipoMovimiento.ENTRADA);
            entrada.setCantidad(orden.getCantidad());
            entrada.setProducto(orden.getProducto());
            entrada.setBodegaDestino(orden.getBodegaDestino());
            movimientoRepository.save(entrada);
        }

        orden.setEstado(nuevoEstado);
        // Al cambiar de estado, el PDF guardado queda invalidado (debe regenerarse)
        orden.setPdfDocumento(null);
        orden.setPdfFechaGeneracion(null);

        return ordenCompraRepository.save(orden);
    }

    private void validarTransicion(EstadoOrden actual, EstadoOrden nuevo) {
        boolean permitida = switch (actual) {
            case BORRADOR -> nuevo == EstadoOrden.APROBADA || nuevo == EstadoOrden.CANCELADA;
            case APROBADA -> nuevo == EstadoOrden.RECIBIDA || nuevo == EstadoOrden.CANCELADA;
            case RECIBIDA, CANCELADA -> false;
        };

        if (!permitida) {
            throw new TransicionInvalidaException(
                    "No se puede pasar una orden de " + actual + " a " + nuevo);
        }
    }
}