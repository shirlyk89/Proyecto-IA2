package com.proyectospring.gestionbodega.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.proyectospring.gestionbodega.entities.Bodega;
import com.proyectospring.gestionbodega.entities.Movimiento;
import com.proyectospring.gestionbodega.entities.Producto;
import com.proyectospring.gestionbodega.entities.TipoMovimiento;
import com.proyectospring.gestionbodega.entities.Usuario;
import com.proyectospring.gestionbodega.repositories.BodegaRepository;
import com.proyectospring.gestionbodega.repositories.MovimientoRepository;
import com.proyectospring.gestionbodega.repositories.ProductoRepository;
import com.proyectospring.gestionbodega.repositories.UsuarioRepository;
import com.proyectospring.gestionbodega.security.dtos.MovimientoDto;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceImplTest {

    @Mock
    private MovimientoRepository movimientoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private BodegaRepository bodegaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private MovimientoServiceImpl movimientoService;

    @Test
    void registrarEntradaDebeAumentarStock() {

        Producto producto = new Producto();
        producto.setId(1L);
        producto.setStock(10);

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Bodega destino = new Bodega();
        destino.setId(1L);

        MovimientoDto dto = new MovimientoDto();
        dto.setProducto(producto);
        dto.setUsuario(usuario);
        dto.setBodegaDestino(destino);
        dto.setCantidad(5);
        dto.setTipo(TipoMovimiento.ENTRADA);

        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto));

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(bodegaRepository.findById(1L))
                .thenReturn(Optional.of(destino));

        when(movimientoRepository.save(any(Movimiento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Movimiento resultado = movimientoService.registrarMovimiento(dto);

        assertEquals(15, producto.getStock());

        verify(productoRepository).save(producto);
        verify(movimientoRepository).save(any(Movimiento.class));

        assertEquals(TipoMovimiento.ENTRADA, resultado.getTipo());
    }

    @Test
    void registrarSalidaDebeDisminuirStock() {

        Producto producto = new Producto();
        producto.setId(1L);
        producto.setStock(20);

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        MovimientoDto dto = new MovimientoDto();
        dto.setProducto(producto);
        dto.setUsuario(usuario);
        dto.setCantidad(5);
        dto.setTipo(TipoMovimiento.SALIDA);

        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto));

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(movimientoRepository.save(any(Movimiento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        movimientoService.registrarMovimiento(dto);

        assertEquals(15, producto.getStock());

        verify(productoRepository).save(producto);
        verify(movimientoRepository).save(any(Movimiento.class));
    }

    @Test
    void registrarSalidaConStockInsuficienteDebeLanzarExcepcion() {

        Producto producto = new Producto();
        producto.setId(1L);
        producto.setStock(3);

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        MovimientoDto dto = new MovimientoDto();
        dto.setProducto(producto);
        dto.setUsuario(usuario);
        dto.setCantidad(10);
        dto.setTipo(TipoMovimiento.SALIDA);

        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto));

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> movimientoService.registrarMovimiento(dto));

        assertEquals("Stock insuficiente para realizar la salida",
                ex.getMessage());

        verify(productoRepository, never()).save(any());
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    void registrarMovimientoConProductoInexistenteDebeLanzarExcepcion() {

        Producto producto = new Producto();
        producto.setId(100L);

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        MovimientoDto dto = new MovimientoDto();
        dto.setProducto(producto);
        dto.setUsuario(usuario);
        dto.setCantidad(5);
        dto.setTipo(TipoMovimiento.ENTRADA);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(productoRepository.findById(100L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> movimientoService.registrarMovimiento(dto));

        assertEquals("El producto no existe", ex.getMessage());
    }

    @Test
    void registrarMovimientoConUsuarioInexistenteDebeLanzarExcepcion() {

        Usuario usuario = new Usuario();
        usuario.setId(50L);

        Producto producto = new Producto();
        producto.setId(1L);

        MovimientoDto dto = new MovimientoDto();
        dto.setUsuario(usuario);
        dto.setProducto(producto);
        dto.setCantidad(3);
        dto.setTipo(TipoMovimiento.ENTRADA);

        when(usuarioRepository.findById(50L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> movimientoService.registrarMovimiento(dto));

        assertEquals("El usuario no existe", ex.getMessage());
    }

    @Test
    void listarTodosDebeRetornarLista() {

        List<Movimiento> movimientos = List.of(
                new Movimiento(),
                new Movimiento());

        when(movimientoRepository.findAll())
                .thenReturn(movimientos);

        List<Movimiento> resultado = movimientoService.listarTodos();

        assertEquals(2, resultado.size());

        verify(movimientoRepository).findAll();
    }

    @Test
    void buscarPorRangoDebeRetornarMovimientos() {

        LocalDateTime inicio = LocalDateTime.now().minusDays(5);
        LocalDateTime fin = LocalDateTime.now();

        List<Movimiento> movimientos = List.of(new Movimiento());

        when(movimientoRepository.buscarPorRangoDeFechas(inicio, fin))
                .thenReturn(movimientos);

        List<Movimiento> resultado =
                movimientoService.buscarPorRangoDeFechas(inicio, fin);

        assertEquals(1, resultado.size());

        verify(movimientoRepository)
                .buscarPorRangoDeFechas(inicio, fin);
    }
}