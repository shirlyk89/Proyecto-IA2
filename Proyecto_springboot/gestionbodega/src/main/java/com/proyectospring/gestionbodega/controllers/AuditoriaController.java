package com.proyectospring.gestionbodega.controllers;

import com.proyectospring.gestionbodega.entities.Auditoria;
import com.proyectospring.gestionbodega.repositories.AuditoriaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaController(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    // 1. Obtener toda la lista de auditorías
    @GetMapping
    public ResponseEntity<List<Auditoria>> obtenerTodas() {
        return ResponseEntity.ok(auditoriaRepository.findAll());
    }

    // 2. Filtrar auditorías por nombre de usuario
    @GetMapping("/usuario/{usuario}")
    public ResponseEntity<List<Auditoria>> obtenerPorUsuario(@PathVariable String usuario) {
        return ResponseEntity.ok(auditoriaRepository.findByUsuario(usuario));
    }

    // 3. Filtrar por tipo de operación (INSERT, UPDATE, DELETE)
    @GetMapping("/operacion/{operacion}")
    public ResponseEntity<List<Auditoria>> obtenerPorOperacion(@PathVariable String operacion) {
        return ResponseEntity.ok(auditoriaRepository.findByOperacion(operacion.toUpperCase()));
    }
}