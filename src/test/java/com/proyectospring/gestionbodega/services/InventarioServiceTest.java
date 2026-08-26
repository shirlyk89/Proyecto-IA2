package com.proyectospring.gestionbodega.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.proyectospring.gestionbodega.entities.Bodega;
import com.proyectospring.gestionbodega.entities.EstadoCobertura;
import com.proyectospring.gestionbodega.entities.Movimiento;
import com.proyectospring.gestionbodega.entities.Producto;
import com.proyectospring.gestionbodega.entities.TipoMovimiento;
import com.proyectospring.gestionbodega.repositories.MovimientoRepository;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private MovimientoRepository movimientoRepository;

    @InjectMocks
    private InventarioServiceImpl inventarioService;

    private Bodega bodegaA;
    private Bodega bodegaB;
    private Producto producto;
    private final LocalDate hoy = LocalDate.of(2026, 8, 26);

    @BeforeEach
    void setUp() {
        bodegaA = new Bodega();
        bodegaA.setId(1L);

        bodegaB = new Bodega();
        bodegaB.setId(2L);

        producto = new Producto();
        producto.setId(10L);
    }

    private Movimiento movimiento(TipoMovimiento tipo, Integer cantidad, Bodega origen, Bodega destino, LocalDateTime fechaHora) {
        return Movimiento.builder()
                .tipo(tipo)
                .cantidad(cantidad)
                .producto(producto)
                .bodegaOrigen(origen)
                .bodegaDestino(destino)
                .fechaHora(fechaHora)
                .build();
    }

    @Test
    void stockTotalSumaEntradasSalidasYTransferencias() {
        when(movimientoRepository.findByProductoId(10L)).thenReturn(Arrays.asList(
                movimiento(TipoMovimiento.ENTRADA, 100, null, bodegaA, hoy.atStartOfDay()),
                movimiento(TipoMovimiento.SALIDA, 20, bodegaA, null, hoy.atStartOfDay()),
                movimiento(TipoMovimiento.TRANSFERENCIA, 30, bodegaA, bodegaB, hoy.atStartOfDay())
        ));

        int stockTotal = inventarioService.calcularStockTotal(10L);

        // 100 entrada - 20 salida - 30 transferencia (sale de A) + 30 transferencia (entra a B) = 50
        assertEquals(80, stockTotal);
    }

    @Test
    void consumoDiarioPromedioCero_devuelveNullYEstadoSinConsumo() {
        when(movimientoRepository.findByProductoId(10L)).thenReturn(Arrays.asList(
                movimiento(TipoMovimiento.ENTRADA, 100, null, bodegaA, hoy.atStartOfDay())
        ));

        BigDecimal consumo = inventarioService.calcularConsumoDiarioPromedio(10L, hoy);
        assertNull(consumo);

        EstadoCobertura estado = inventarioService.calcularEstadoCobertura(100, consumo, null);
        assertEquals(EstadoCobertura.SIN_CONSUMO, estado);

        BigDecimal cobertura = inventarioService.calcularDiasCobertura(100, consumo);
        assertNull(cobertura);
    }

    @Test
    void stockIgualAlPuntoReorden_noEstaEnRiesgo() {
        BigDecimal puntoReorden = new BigDecimal("50");

        boolean enRiesgo = inventarioService.estaEnRiesgo(50, puntoReorden);

        assertFalse(enRiesgo, "El stock igual al punto de reorden NO debe considerarse en riesgo");
    }

    @Test
    void stockMenorAlPuntoReorden_siEstaEnRiesgo() {
        BigDecimal puntoReorden = new BigDecimal("50");

        boolean enRiesgo = inventarioService.estaEnRiesgo(49, puntoReorden);

        assertTrue(enRiesgo);
    }

    @Test
    void puntoReordenSeCalculaConLaFormulaExacta() {
        BigDecimal consumoDiarioPromedio = new BigDecimal("4.0000");
        Integer diasEntrega = 5;

        BigDecimal puntoReorden = inventarioService.calcularPuntoReorden(consumoDiarioPromedio, diasEntrega);

        // 4.0 * 5 * 1.5 = 30.0
        assertEquals(0, new BigDecimal("30.00").compareTo(puntoReorden));
    }

    @Test
    void consumoDiarioPromedioSoloCuentaSalidasDeUltimos30DiasIncluidaFechaConsulta() {
        when(movimientoRepository.findByProductoId(10L)).thenReturn(Arrays.asList(
                // dentro de los 30 días (incluido el día de consulta)
                movimiento(TipoMovimiento.SALIDA, 30, bodegaA, null, hoy.atStartOfDay()),
                movimiento(TipoMovimiento.SALIDA, 30, bodegaA, null, hoy.minusDays(29).atStartOfDay()),
                // fuera de los 30 días, no debe contarse
                movimiento(TipoMovimiento.SALIDA, 999, bodegaA, null, hoy.minusDays(31).atStartOfDay()),
                // ENTRADA no cuenta como consumo
                movimiento(TipoMovimiento.ENTRADA, 500, null, bodegaA, hoy.atStartOfDay())
        ));

        BigDecimal consumo = inventarioService.calcularConsumoDiarioPromedio(10L, hoy);

        // (30 + 30) / 30 = 2.0
        assertEquals(0, new BigDecimal("2.0000").compareTo(consumo));
    }
}