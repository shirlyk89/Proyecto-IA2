package com.proyectospring.gestionbodega.services;

import java.util.List;

import com.proyectospring.gestionbodega.entities.Movimiento;
import com.proyectospring.gestionbodega.security.dtos.MovimientoDto;
import java.time.LocalDateTime;


public interface MovimientoService {

    Movimiento registrarMovimiento(MovimientoDto dto);

    List<Movimiento> listarTodos();



// Dentro de public interface MovimientoService:
List<Movimiento> buscarPorRangoDeFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    
} 
