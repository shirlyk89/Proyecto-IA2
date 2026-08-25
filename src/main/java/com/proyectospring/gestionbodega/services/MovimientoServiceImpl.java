package com.proyectospring.gestionbodega.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.proyectospring.gestionbodega.entities.Bodega;
import com.proyectospring.gestionbodega.entities.Movimiento;
import com.proyectospring.gestionbodega.entities.Producto;
import com.proyectospring.gestionbodega.entities.Usuario;
import com.proyectospring.gestionbodega.repositories.BodegaRepository;
import com.proyectospring.gestionbodega.repositories.MovimientoRepository;
import com.proyectospring.gestionbodega.repositories.ProductoRepository;
import com.proyectospring.gestionbodega.repositories.UsuarioRepository;
import com.proyectospring.gestionbodega.security.dtos.MovimientoDto;

import jakarta.transaction.Transactional;

@Service

public class MovimientoServiceImpl implements MovimientoService {

    
    private final MovimientoRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final BodegaRepository bodegaRepository;
    private final UsuarioRepository usuarioRepository;

   

    public MovimientoServiceImpl(MovimientoRepository movimientoRepository, ProductoRepository productoRepository,
          BodegaRepository bodegaRepository, UsuarioRepository usuarioRepository
    ) {
        this.movimientoRepository = movimientoRepository;
        this.productoRepository = productoRepository;
        this.bodegaRepository = bodegaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public List<Movimiento> buscarPorRangoDeFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return movimientoRepository.buscarPorRangoDeFechas(fechaInicio, fechaFin);
    }


  
    @Override
    @Transactional
    public Movimiento registrarMovimiento(MovimientoDto dto) {

        if (dto.getUsuario() != null && dto.getUsuario().getId() != null) {
            Usuario usuario = usuarioRepository.findById(dto.getUsuario().getId())
                    .orElseThrow(() -> new RuntimeException("El usuario no existe"));
            dto.setUsuario(usuario);
        }


        if (dto.getBodegaDestino() != null && dto.getBodegaDestino().getId() != null) {
            Bodega bodegaDestino = bodegaRepository.findById(dto.getBodegaDestino().getId())
                    .orElseThrow(() -> new RuntimeException("La bodega destino no existe"));
            dto.setBodegaDestino(bodegaDestino);
        }

        // 3. ➕ NUEVO: Cargar los datos de la bodega origen si viene en el JSON
        if (dto.getBodegaOrigen() != null && dto.getBodegaOrigen().getId() != null) {
            Bodega bodegaOrigen = bodegaRepository.findById(dto.getBodegaOrigen().getId())
                    .orElseThrow(() -> new RuntimeException("La bodega origen no existe"));
            dto.setBodegaOrigen(bodegaOrigen);
        }
        
        Producto producto = productoRepository.findById(dto.getProducto().getId())
                .orElseThrow(() -> new RuntimeException("El producto no existe"));

        switch (dto.getTipo()) {

            case ENTRADA:
                int nuevoStockEntrada = producto.getStock() + dto.getCantidad();
                producto.setStock(nuevoStockEntrada);
                break;

            case SALIDA:
                if (producto.getStock() < dto.getCantidad()) {
                    throw new RuntimeException("Stock insuficiente para realizar la salida");
                }
                int nuevoStockSalida = producto.getStock() - dto.getCantidad();
                producto.setStock(nuevoStockSalida);
                break;

            case TRANSFERENCIA:
                if (producto.getStock() < dto.getCantidad()) {
                    throw new RuntimeException("Stock insuficiente para realizar la transferencia");
                }
                break;
        }

        productoRepository.save(producto);

        Movimiento movimiento = new Movimiento();
        movimiento.setUsuario(dto.getUsuario());
        movimiento.setBodegaDestino(dto.getBodegaDestino());
        movimiento.setBodegaOrigen(dto.getBodegaOrigen());
        movimiento.setProducto(producto);
        movimiento.setTipo(dto.getTipo());
        movimiento.setCantidad(dto.getCantidad());

        return movimientoRepository.save(movimiento);
    }

    @Override
    public List<Movimiento> listarTodos() {
        return movimientoRepository.findAll();
    }

}
