package com.proyectospring.gestionbodega.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyectospring.gestionbodega.entities.Bodega;
import com.proyectospring.gestionbodega.services.BodegaService;

@RestController
@RequestMapping("/api/bodegas")
public class BodegaController {

    private final BodegaService bodegaService;


    public BodegaController(BodegaService bodegaService) {
        this.bodegaService = bodegaService;
    }


    @GetMapping
    public List<Bodega> getAll() {
        return bodegaService.findAll();
    }


    @GetMapping("/{id}")
    public Optional<Bodega> getById(@PathVariable Long id) {
        return bodegaService.findById(id);
    }

    @PostMapping
    public Bodega create(@RequestBody Bodega bodega) {
        return bodegaService.save(bodega);
    }

    @PutMapping("/{id}")
    public Bodega update(@PathVariable Long id, @RequestBody Bodega bodega) {
        return bodegaService.update(id, bodega);
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bodegaService.deleteById(id);
    }
}
