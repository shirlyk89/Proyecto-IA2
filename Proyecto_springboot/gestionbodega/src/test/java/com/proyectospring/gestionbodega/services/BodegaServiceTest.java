package com.proyectospring.gestionbodega.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.proyectospring.gestionbodega.entities.Bodega;
import com.proyectospring.gestionbodega.repositories.BodegaRepository;


@ExtendWith(MockitoExtension.class)
class BodegaServiceTest {


    @Mock
    private BodegaRepository bodegaRepository;


    @InjectMocks
    private BodegaService bodegaService;


    private Bodega bodega;


    @BeforeEach
    void setUp() {

        bodega = new Bodega(
                1L,
                "Bodega Principal",
                "Bogotá",
                500,
                "Carlos", 
                List.of(), 
                List.of()
        );
    }



    @Test
    void findAllDebeRetornarBodegas() {

        when(bodegaRepository.findAll())
                .thenReturn(Arrays.asList(bodega));


        List<Bodega> resultado =
                bodegaService.findAll();


        assertEquals(1, resultado.size());
        assertEquals("Bodega Principal",
                resultado.get(0).getNombre());

        verify(bodegaRepository)
                .findAll();
    }



    @Test
    void findByIdDebeRetornarBodega() {

        when(bodegaRepository.findById(1L))
                .thenReturn(Optional.of(bodega));


        Optional<Bodega> resultado =
                bodegaService.findById(1L);


        assertTrue(resultado.isPresent());
        assertEquals("Bodega Principal",
                resultado.get().getNombre());

        verify(bodegaRepository)
                .findById(1L);
    }



    @Test
    void saveDebeGuardarBodega() {

        when(bodegaRepository.save(bodega))
                .thenReturn(bodega);


        Bodega resultado =
                bodegaService.save(bodega);


        assertNotNull(resultado);
        assertEquals("Bodega Principal",
                resultado.getNombre());

        verify(bodegaRepository)
                .save(bodega);
    }



    @Test
    void updateDebeActualizarBodega() {

        Bodega cambios = new Bodega();

        cambios.setNombre("Bodega Secundaria");
        cambios.setUbicacion("Medellín");
        cambios.setCapacidad(300);
        cambios.setEncargado("Ana");


        when(bodegaRepository.findById(1L))
                .thenReturn(Optional.of(bodega));

        when(bodegaRepository.save(bodega))
                .thenReturn(bodega);



        Bodega resultado =
                bodegaService.update(1L, cambios);



        assertEquals(
                "Bodega Secundaria",
                resultado.getNombre()
        );

        assertEquals(
                "Medellín",
                resultado.getUbicacion()
        );


        verify(bodegaRepository)
                .save(bodega);
    }



    @Test
    void updateBodegaNoExisteDebeLanzarError() {

        when(bodegaRepository.findById(1L))
                .thenReturn(Optional.empty());


        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> bodegaService.update(1L, bodega)
                );


        assertEquals(
                "No se ha encontrado ninguna Bodega con el ID: 1",
                exception.getMessage()
        );
    }



    @Test
    void deleteDebeEliminarBodega() {

        when(bodegaRepository.existsById(1L))
                .thenReturn(true);


        bodegaService.deleteById(1L);


        verify(bodegaRepository)
                .deleteById(1L);
    }



    @Test
    void deleteBodegaNoExisteDebeLanzarError() {

        when(bodegaRepository.existsById(1L))
                .thenReturn(false);


        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> bodegaService.deleteById(1L)
                );


        assertEquals(
                "No se puede eliminar. No existe ninguna Bodega con el ID: 1",
                exception.getMessage()
        );


        verify(bodegaRepository, never())
                .deleteById(1L);
    }

}