package com.proyectospring.gestionbodega.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "auditorias", schema = "logitrack")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String entidadAfectada;
    
    private String idEntidad;
    
    private String operacion; // INSERT, UPDATE, DELETE
    
    private String usuario; // Quién hizo la acción
    
    private LocalDateTime fecha;

    public Auditoria() {
        this.fecha = LocalDateTime.now();
    }

    public Auditoria(String entidadAfectada, String idEntidad, String operacion, String usuario) {
        this.entidadAfectada = entidadAfectada;
        this.idEntidad = idEntidad;
        this.operacion = operacion;
        this.usuario = usuario;
        this.fecha = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEntidadAfectada() { return entidadAfectada; }
    public void setEntidadAfectada(String entidadAfectada) { this.entidadAfectada = entidadAfectada; }
    public String getIdEntidad() { return idEntidad; }
    public void setIdEntidad(String idEntidad) { this.idEntidad = idEntidad; }
    public String getOperacion() { return operacion; }
    public void setOperacion(String operacion) { this.operacion = operacion; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}