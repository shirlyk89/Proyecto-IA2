package com.proyectospring.gestionbodega.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import com.proyectospring.gestionbodega.entities.EstadoCobertura;

public interface InventarioService {

    /** Stock total del producto sumando todas las bodegas, calculado desde movimientos. */
    int calcularStockTotal(Long productoId);

    /** Stock del producto desglosado por bodega (bodegaId -> unidades), calculado desde movimientos. */
    Map<Long, Integer> calcularStockPorBodega(Long productoId);

    /** Unidades en SALIDA de los últimos 30 días calendario (incluida la fecha de referencia) / 30.
     *  Devuelve null si no hubo consumo. */
    BigDecimal calcularConsumoDiarioPromedio(Long productoId, LocalDate fechaReferencia);

    /** consumoDiarioPromedio * diasEntrega * 1.5. Null si el consumo es null. */
    BigDecimal calcularPuntoReorden(BigDecimal consumoDiarioPromedio, Integer diasEntrega);

    /** stockTotal / consumoDiarioPromedio. Null si el consumo es null o 0. */
    BigDecimal calcularDiasCobertura(int stockTotal, BigDecimal consumoDiarioPromedio);

    /** SIN_CONSUMO si no hay consumo; EN_RIESGO si stock < puntoReorden; NORMAL en otro caso. */
    EstadoCobertura calcularEstadoCobertura(int stockTotal, BigDecimal consumoDiarioPromedio, BigDecimal puntoReorden);

    /** true solo si stockTotal es estrictamente menor que puntoReorden (igual NO cuenta como riesgo). */
    boolean estaEnRiesgo(int stockTotal, BigDecimal puntoReorden);
}