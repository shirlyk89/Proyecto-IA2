package com.proyectospring.gestionbodega.services;

import com.proyectospring.gestionbodega.entities.OrdenCompra;

public interface PdfService {
    /** Genera el PDF de la orden. Si está en BORRADOR, incluye la marca de agua diagonal. */
    byte[] generarPdf(OrdenCompra orden);
}
