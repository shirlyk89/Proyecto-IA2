package com.proyectospring.gestionbodega.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.proyectospring.gestionbodega.entities.Bodega;
import com.proyectospring.gestionbodega.repositories.BodegaRepository;

@Service
public class BodegaService {

     private final BodegaRepository bodegaRepository;

    public BodegaService(BodegaRepository bodegaRepository) {
        this.bodegaRepository = bodegaRepository;
    }

    public List<Bodega> findAll() {
        return bodegaRepository.findAll();
    }

    public Optional<Bodega> findById(Long id) {
        return bodegaRepository.findById(id);
    }

    public Bodega save(Bodega bodega) {
        return bodegaRepository.save(bodega);
    }

    public Bodega update(Long id, Bodega bodega) {
        Bodega existing = bodegaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se ha encontrado ninguna Bodega con el ID: " + id));

        existing.setNombre(bodega.getNombre());
        existing.setUbicacion(bodega.getUbicacion());
        existing.setCapacidad(bodega.getCapacidad());
        existing.setEncargado(bodega.getEncargado());

        return bodegaRepository.save(existing);
    }

    public void deleteById(Long id) {
        if (!bodegaRepository.existsById(id)) {
            throw new IllegalArgumentException("No se puede eliminar. No existe ninguna Bodega con el ID: " + id);
        }
        bodegaRepository.deleteById(id);
    }

  

}
