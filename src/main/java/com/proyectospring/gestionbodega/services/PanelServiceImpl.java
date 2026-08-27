package com.proyectospring.gestionbodega.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.proyectospring.gestionbodega.security.dtos.KpiDto;
import com.proyectospring.gestionbodega.security.dtos.MovimientosAyerDto;
import com.proyectospring.gestionbodega.security.dtos.OcupacionBodegaDto;
import com.proyectospring.gestionbodega.security.dtos.OrdenesPorAprobarDto;
import com.proyectospring.gestionbodega.security.dtos.ProductoRiesgoDto;
import com.proyectospring.gestionbodega.security.dtos.StockProductoDto;
import com.proyectospring.gestionbodega.entities.Bodega;
import com.proyectospring.gestionbodega.entities.EstadoCobertura;
import com.proyectospring.gestionbodega.entities.EstadoOrden;
import com.proyectospring.gestionbodega.entities.Producto;
import com.proyectospring.gestionbodega.entities.TipoMovimiento;
import com.proyectospring.gestionbodega.repositories.BodegaRepository;
import com.proyectospring.gestionbodega.repositories.MovimientoRepository;
import com.proyectospring.gestionbodega.repositories.OrdenCompraRepository;
import com.proyectospring.gestionbodega.repositories.ProductoRepository;

@Service
public class PanelServiceImpl implements PanelService {

    private static final ZoneId ZONA = ZoneId.of("America/Bogota");

    private final InventarioService inventarioService;
    private final ProductoRepository productoRepository;
    private final BodegaRepository bodegaRepository;
    private final OrdenCompraRepository ordenCompraRepository;
    private final MovimientoRepository movimientoRepository;

    public PanelServiceImpl(InventarioService inventarioService,
                             ProductoRepository productoRepository,
                             BodegaRepository bodegaRepository,
                             OrdenCompraRepository ordenCompraRepository,
                             MovimientoRepository movimientoRepository) {
        this.inventarioService = inventarioService;
        this.productoRepository = productoRepository;
        this.bodegaRepository = bodegaRepository;
        this.ordenCompraRepository = ordenCompraRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @Override
    public KpiDto calcularKpis() {
        List<Producto> productos = productoRepository.findAll();
        List<Bodega> bodegas = bodegaRepository.findAll();

        // Ocupación por bodega: acumular stock de todos los productos por bodega
        Map<Long, Integer> stockGlobalPorBodega = new java.util.HashMap<>();
        long productosEnQuiebre = 0;
        for (Producto p : productos) {
            Map<Long, Integer> porBodega = inventarioService.calcularStockPorBodega(p.getId());
            porBodega.forEach((bodegaId, cantidad) ->
                    stockGlobalPorBodega.merge(bodegaId, cantidad, Integer::sum));

            int total = porBodega.values().stream().mapToInt(Integer::intValue).sum();
            if (total == 0) {
                productosEnQuiebre++;
            }
        }

        List<OcupacionBodegaDto> ocupacion = new ArrayList<>();
        for (Bodega b : bodegas) {
            int unidades = stockGlobalPorBodega.getOrDefault(b.getId(), 0);
            BigDecimal porcentaje = BigDecimal.valueOf(unidades)
                    .divide(BigDecimal.valueOf(b.getCapacidad()), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
            ocupacion.add(new OcupacionBodegaDto(b.getId(), b.getNombre(), porcentaje));
        }

        List<ProductoRiesgoDto> enRiesgo = listarProductosEnRiesgo();

        List<com.proyectospring.gestionbodega.entities.OrdenCompra> borradores =
                ordenCompraRepository.findByEstado(EstadoOrden.BORRADOR);
        BigDecimal montoTotalBorradores = borradores.stream()
                .map(com.proyectospring.gestionbodega.entities.OrdenCompra::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        MovimientosAyerDto movimientosAyer = contarMovimientosAyer();

        OffsetDateTime ahora = OffsetDateTime.now(ZONA);

        return new KpiDto(
                ahora,
                ocupacion,
                productosEnQuiebre,
                enRiesgo.size(),
                new OrdenesPorAprobarDto(borradores.size(), montoTotalBorradores),
                movimientosAyer
        );
    }

    @Override
    public List<ProductoRiesgoDto> listarProductosEnRiesgo() {
        List<ProductoRiesgoDto> resultado = new ArrayList<>();
        LocalDate hoy = LocalDate.now(ZONA);

        for (Producto p : productoRepository.findAll()) {
            if (p.getProveedorPrincipal() == null) {
                continue; // regla: sin proveedor principal no puede estar en riesgo
            }

            int stockTotal = inventarioService.calcularStockTotal(p.getId());
            BigDecimal consumo = inventarioService.calcularConsumoDiarioPromedio(p.getId(), hoy);
            BigDecimal puntoReorden = inventarioService.calcularPuntoReorden(
                    consumo, p.getProveedorPrincipal().getDiasEntrega());

            if (!inventarioService.estaEnRiesgo(stockTotal, puntoReorden)) {
                continue;
            }

            BigDecimal diasCobertura = inventarioService.calcularDiasCobertura(stockTotal, consumo);
            EstadoCobertura estado = inventarioService.calcularEstadoCobertura(stockTotal, consumo, puntoReorden);

            Long bodegaDestinoSugerida = sugerirBodegaDestino(p.getId());

            resultado.add(new ProductoRiesgoDto(
                    p.getId(),
                    p.getNombre(),
                    p.getProveedorPrincipal().getId(),
                    stockTotal,
                    consumo,
                    puntoReorden,
                    diasCobertura,
                    estado,
                    bodegaDestinoSugerida
            ));
        }
        return resultado;
    }

    /** Bodega con menor stock del producto; si hay empate, la de menor id. */
    private Long sugerirBodegaDestino(Long productoId) {
        Map<Long, Integer> porBodega = inventarioService.calcularStockPorBodega(productoId);
        return porBodega.entrySet().stream()
                .sorted((a, b) -> {
                    int cmp = a.getValue().compareTo(b.getValue());
                    return cmp != 0 ? cmp : a.getKey().compareTo(b.getKey());
                })
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<OcupacionBodegaDto> listarBodegasCriticas() {
        KpiDto kpis = calcularKpis();
        return kpis.getOcupacionPorBodega().stream()
                .filter(o -> o.getPorcentaje().compareTo(BigDecimal.valueOf(90)) >= 0)
                .toList();
    }

    @Override
    public StockProductoDto obtenerStockProducto(Long productoId) {
        int total = inventarioService.calcularStockTotal(productoId);
        Map<Long, Integer> porBodega = inventarioService.calcularStockPorBodega(productoId);
        return new StockProductoDto(total, porBodega);
    }

    private MovimientosAyerDto contarMovimientosAyer() {
        LocalDate hoy = LocalDate.now(ZONA);
        LocalDate ayer = hoy.minusDays(1);
        LocalDateTime inicio = ayer.atStartOfDay();
        LocalDateTime fin = hoy.atStartOfDay().minusNanos(1);

        List<com.proyectospring.gestionbodega.entities.Movimiento> movimientos =
                movimientoRepository.findByFechaHoraBetween(inicio, fin);

        long entrada = movimientos.stream().filter(m -> m.getTipo() == TipoMovimiento.ENTRADA).count();
        long salida = movimientos.stream().filter(m -> m.getTipo() == TipoMovimiento.SALIDA).count();
        long transferencia = movimientos.stream().filter(m -> m.getTipo() == TipoMovimiento.TRANSFERENCIA).count();

        return new MovimientosAyerDto(entrada, salida, transferencia);
    }
}