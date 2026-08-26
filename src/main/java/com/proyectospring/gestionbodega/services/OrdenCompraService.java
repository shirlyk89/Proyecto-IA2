package com.proyectospring.gestionbodega.services;

import com.proyectospring.gestionbodega.entities.EstadoOrden;
import com.proyectospring.gestionbodega.entities.OrdenCompra;

public interface OrdenCompraService {

    /** Crea la orden en estado BORRADOR, calculando el total en el servidor. */
    OrdenCompra crearBorrador(OrdenCompra orden);

    /** Cambia el estado según la matriz de transiciones permitidas.
     *  Si pasa de APROBADA a RECIBIDA, crea el movimiento ENTRADA en la misma transacción. */
    OrdenCompra cambiarEstado(Long ordenId, EstadoOrden nuevoEstado);
}