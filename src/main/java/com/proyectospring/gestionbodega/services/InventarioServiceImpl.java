package com.proyectospring.gestionbodega.services;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.proyectospring.gestionbodega.entities.Bodega;
import com.proyectospring.gestionbodega.entities.EstadoCobertura;
import com.proyectospring.gestionbodega.entities.Movimiento;
import com.proyectospring.gestionbodega.repositories.MovimientoRepository;

@Service
public class InventarioServiceImpl implements InventarioService {

    private final MovimientoRepository movimientoRepository;

    public InventarioServiceImpl(MovimientoRepository movimientoRepository) {
        this.movimientoRepository = movimientoRepository;
    }

    @Override
    public int calcularStockTotal(Long productoId) {
        return calcularStockPorBodega(productoId).values().stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    @Override
    public Map<Long, Integer> calcularStockPorBodega(Long productoId) {
        List<Movimiento> movimientos = movimientoRepository.findByProductoId(productoId);
        Map<Long, Integer> stockPorBodega = new HashMap<>();

        for (Movimiento m : movimientos) {
            switch (m.getTipo()) {
                case ENTRADA:
                    sumar(stockPorBodega, m.getBodegaDestino(), m.getCantidad());
                    break;
                case SALIDA:
                    sumar(stockPorBodega, m.getBodegaOrigen(), -m.getCantidad());
                    break;
                case TRANSFERENCIA:
                    sumar(stockPorBodega, m.getBodegaOrigen(), -m.getCantidad());
                    sumar(stockPorBodega, m.getBodegaDestino(), m.getCantidad());
                    break;
            }
        }
        return stockPorBodega;
    }

    private void sumar(Map<Long, Integer> stockPorBodega, Bodega bodega, int cantidad) {
        if (bodega == null || bodega.getId() == null) {
            return;
        }
        stockPorBodega.merge(bodega.getId(), cantidad, Integer::sum);
    }

    @Override
    public BigDecimal calcularConsumoDiarioPromedio(Long productoId, LocalDate fechaReferencia) {
        LocalDate desde = fechaReferencia.minusDays(29); // 30 días incluida la fecha de consulta

        List<Movimiento> movimientos = movimientoRepository.findByProductoId(productoId);

        int totalSalidas = movimientos.stream()
                .filter(m -> m.getTipo() == com.proyectospring.gestionbodega.entities.TipoMovimiento.SALIDA)
                .filter(m -> {
                    LocalDate fecha = m.getFechaHora().toLocalDate();
                    return !fecha.isBefore(desde) && !fecha.isAfter(fechaReferencia);
                })
                .mapToInt(Movimiento::getCantidad)
                .sum();

        if (totalSalidas == 0) {
            return null;
        }

        return new BigDecimal(totalSalidas).divide(new BigDecimal("30"), 4, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcularPuntoReorden(BigDecimal consumoDiarioPromedio, Integer diasEntrega) {
        if (consumoDiarioPromedio == null) {
            return null;
        }
        return consumoDiarioPromedio
                .multiply(new BigDecimal(diasEntrega))
                .multiply(new BigDecimal("1.5"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcularDiasCobertura(int stockTotal, BigDecimal consumoDiarioPromedio) {
        if (consumoDiarioPromedio == null || consumoDiarioPromedio.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return new BigDecimal(stockTotal).divide(consumoDiarioPromedio, new MathContext(6));
    }

    @Override
    public EstadoCobertura calcularEstadoCobertura(int stockTotal, BigDecimal consumoDiarioPromedio, BigDecimal puntoReorden) {
        if (consumoDiarioPromedio == null) {
            return EstadoCobertura.SIN_CONSUMO;
        }
        if (estaEnRiesgo(stockTotal, puntoReorden)) {
            return EstadoCobertura.EN_RIESGO;
        }
        return EstadoCobertura.NORMAL;
    }

    @Override
    public boolean estaEnRiesgo(int stockTotal, BigDecimal puntoReorden) {
        if (puntoReorden == null) {
            return false;
        }
        return new BigDecimal(stockTotal).compareTo(puntoReorden) < 0;
    }
}