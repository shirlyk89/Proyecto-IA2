package com.proyectospring.gestionbodega.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.proyectospring.gestionbodega.entities.Bodega;
import com.proyectospring.gestionbodega.entities.EstadoOrden;
import com.proyectospring.gestionbodega.entities.Movimiento;
import com.proyectospring.gestionbodega.entities.OrdenCompra;
import com.proyectospring.gestionbodega.entities.Producto;
import com.proyectospring.gestionbodega.entities.Proveedor;
import com.proyectospring.gestionbodega.entities.TipoMovimiento;
import com.proyectospring.gestionbodega.exceptions.TransicionInvalidaException;
import com.proyectospring.gestionbodega.repositories.MovimientoRepository;
import com.proyectospring.gestionbodega.repositories.OrdenCompraRepository;

@ExtendWith(MockitoExtension.class)
class OrdenCompraServiceTest {

    @Mock
    private OrdenCompraRepository ordenCompraRepository;

    @Mock
    private MovimientoRepository movimientoRepository;

    @InjectMocks
    private OrdenCompraServiceImpl ordenCompraService;

    private OrdenCompra ordenBorrador;
    private Producto producto;
    private Bodega bodega;

    @BeforeEach
    void setUp() {
        bodega = new Bodega();
        bodega.setId(1L);

        producto = new Producto();
        producto.setId(5L);

        Proveedor proveedor = new Proveedor();
        proveedor.setId(2L);

        ordenBorrador = new OrdenCompra();
        ordenBorrador.setId(100L);
        ordenBorrador.setProducto(producto);
        ordenBorrador.setProveedor(proveedor);
        ordenBorrador.setBodegaDestino(bodega);
        ordenBorrador.setCantidad(10);
        ordenBorrador.setPrecioUnitario(new BigDecimal("5000"));
        ordenBorrador.setTotal(new BigDecimal("50000"));
        ordenBorrador.setEstado(EstadoOrden.BORRADOR);
        ordenBorrador.setFechaCreacion(LocalDateTime.now());
    }

    @Test
    void crearBorrador_calculaTotalYFuerzaEstadoBorrador() {
        OrdenCompra nueva = new OrdenCompra();
        nueva.setProducto(producto);
        nueva.setBodegaDestino(bodega);
        nueva.setCantidad(4);
        nueva.setPrecioUnitario(new BigDecimal("1500"));

        when(ordenCompraRepository.save(any(OrdenCompra.class))).thenAnswer(inv -> inv.getArgument(0));

        OrdenCompra guardada = ordenCompraService.crearBorrador(nueva);

        assertEquals(EstadoOrden.BORRADOR, guardada.getEstado());
        assertEquals(0, new BigDecimal("6000").compareTo(guardada.getTotal()));
        assertNotNull(guardada.getFechaCreacion());
    }

    @Test
    void crearBorrador_cantidadCeroLanzaExcepcion() {
        OrdenCompra nueva = new OrdenCompra();
        nueva.setProducto(producto);
        nueva.setBodegaDestino(bodega);
        nueva.setCantidad(0);
        nueva.setPrecioUnitario(new BigDecimal("1500"));

        assertThrows(IllegalArgumentException.class, () -> ordenCompraService.crearBorrador(nueva));
    }

    @Test
    void borradorAAprobada_esValida() {
        when(ordenCompraRepository.findById(100L)).thenReturn(Optional.of(ordenBorrador));
        when(ordenCompraRepository.save(any(OrdenCompra.class))).thenAnswer(inv -> inv.getArgument(0));

        OrdenCompra resultado = ordenCompraService.cambiarEstado(100L, EstadoOrden.APROBADA);

        assertEquals(EstadoOrden.APROBADA, resultado.getEstado());
    }

    @Test
    void ordenCancelada_noSePuedeAprobar() {
        ordenBorrador.setEstado(EstadoOrden.CANCELADA);
        when(ordenCompraRepository.findById(100L)).thenReturn(Optional.of(ordenBorrador));

        assertThrows(TransicionInvalidaException.class,
                () -> ordenCompraService.cambiarEstado(100L, EstadoOrden.APROBADA));
    }

    @Test
    void borradorNoPuedePasarDirectoARecibida() {
        when(ordenCompraRepository.findById(100L)).thenReturn(Optional.of(ordenBorrador));

        assertThrows(TransicionInvalidaException.class,
                () -> ordenCompraService.cambiarEstado(100L, EstadoOrden.RECIBIDA));
    }

    @Test
    void aprobadaARecibida_creaMovimientoEntradaEnUnaSolaOperacion() {
        ordenBorrador.setEstado(EstadoOrden.APROBADA);
        when(ordenCompraRepository.findById(100L)).thenReturn(Optional.of(ordenBorrador));
        when(ordenCompraRepository.save(any(OrdenCompra.class))).thenAnswer(inv -> inv.getArgument(0));

        ordenCompraService.cambiarEstado(100L, EstadoOrden.RECIBIDA);

        ArgumentCaptor<Movimiento> captor = ArgumentCaptor.forClass(Movimiento.class);
        verify(movimientoRepository).save(captor.capture());

        Movimiento movimientoCreado = captor.getValue();
        assertEquals(TipoMovimiento.ENTRADA, movimientoCreado.getTipo());
        assertEquals(10, movimientoCreado.getCantidad());
        assertEquals(bodega.getId(), movimientoCreado.getBodegaDestino().getId());
        assertEquals(producto.getId(), movimientoCreado.getProducto().getId());
    }
}