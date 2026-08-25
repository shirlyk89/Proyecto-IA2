package com.proyectospring.gestionbodega.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.proyectospring.gestionbodega.entities.Producto;
import com.proyectospring.gestionbodega.repositories.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id);
    }

    public List<Producto> findByStockBajo(Integer limite) {
        return productoRepository.findByStockLessThan(limite);
    }

    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto update(Long id, Producto producto) {
       
        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con el ID: " + id));

      
        productoExistente.setNombre(producto.getNombre());
        productoExistente.setDescripcion(producto.getDescripcion());
        productoExistente.setPrecio(producto.getPrecio());
        productoExistente.setStock(producto.getStock());
        productoExistente.setBodega(producto.getBodega());

        return productoRepository.save(productoExistente);
    }

    
    public void deleteById(Long id) {
        productoRepository.deleteById(id);
    }

}
