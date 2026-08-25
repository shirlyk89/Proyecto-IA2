package com.proyectospring.gestionbodega.services;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import com.proyectospring.gestionbodega.entities.Bodega;
import com.proyectospring.gestionbodega.entities.Producto;
import com.proyectospring.gestionbodega.repositories.ProductoRepository;


@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {


    @Mock
    private ProductoRepository productoRepository;


    @InjectMocks
    private ProductoService productoService;


    private Producto producto;


    @BeforeEach
    void setUp() {

        Bodega bodega = new Bodega();
        bodega.setId(1L);

        producto = new Producto(
                1L,
                "Laptop",
                "Electrónica",
                "Laptop Lenovo",
                new BigDecimal("2500000"),
                10,
                bodega
        );
    }


    @Test
    void findAllDebeRetornarProductos() {

        when(productoRepository.findAll())
                .thenReturn(Arrays.asList(producto));


        List<Producto> resultado = productoService.findAll();


        assertEquals(1, resultado.size());
        assertEquals("Laptop", resultado.get(0).getNombre());

        verify(productoRepository).findAll();
    }



    @Test
    void findByIdDebeRetornarProducto() {

        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto));


        Optional<Producto> resultado = productoService.findById(1L);


        assertTrue(resultado.isPresent());
        assertEquals("Laptop", resultado.get().getNombre());

        verify(productoRepository).findById(1L);
    }



    @Test
    void findByStockBajoDebeRetornarProductos() {

        when(productoRepository.findByStockLessThan(5))
                .thenReturn(Arrays.asList(producto));


        List<Producto> resultado =
                productoService.findByStockBajo(5);


        assertFalse(resultado.isEmpty());

        verify(productoRepository)
                .findByStockLessThan(5);
    }



    @Test
    void saveDebeGuardarProducto() {

        when(productoRepository.save(producto))
                .thenReturn(producto);


        Producto resultado =
                productoService.save(producto);


        assertNotNull(resultado);
        assertEquals("Laptop", resultado.getNombre());

        verify(productoRepository)
                .save(producto);
    }



    @Test
    void updateDebeActualizarProducto() {

        Producto cambios = new Producto();
        cambios.setNombre("Laptop Gamer");
        cambios.setDescripcion("Nueva descripción");
        cambios.setPrecio(new BigDecimal("3000000"));
        cambios.setStock(5);


        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto));

        when(productoRepository.save(producto))
                .thenReturn(producto);


        Producto resultado =
                productoService.update(1L, cambios);


        assertEquals("Laptop Gamer",
                resultado.getNombre());

        assertEquals(5,
                resultado.getStock());

        verify(productoRepository)
                .save(producto);
    }



    @Test
    void updateProductoNoExisteDebeLanzarError() {

        when(productoRepository.findById(1L))
                .thenReturn(Optional.empty());


        RuntimeException exception =
                assertThrows(RuntimeException.class,
                        () -> productoService.update(1L, producto));


        assertEquals(
                "Producto no encontrado con el ID: 1",
                exception.getMessage()
        );
    }



    @Test
    void deleteDebeEliminarProducto() {

        doNothing()
                .when(productoRepository)
                .deleteById(1L);


        productoService.deleteById(1L);


        verify(productoRepository)
                .deleteById(1L);
    }

}