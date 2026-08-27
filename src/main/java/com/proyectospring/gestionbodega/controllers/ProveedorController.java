package com.proyectospring.gestionbodega.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyectospring.gestionbodega.entities.Proveedor;
import com.proyectospring.gestionbodega.repositories.ProveedorRepository;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    private final ProveedorRepository proveedorRepository;

    public ProveedorController(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    @GetMapping
    public List<Proveedor> getAll() {
        return proveedorRepository.findAll();
    }
}