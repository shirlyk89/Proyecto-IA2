package com.proyectospring.gestionbodega.entities;

import java.time.LocalDate;

import com.proyectospring.gestionbodega.security.listeners.AuditoriaListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@EntityListeners(AuditoriaListener.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "resumenes_panel", schema = "logitrack")
public class ResumenPanel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, unique = true)
    private LocalDate fecha;

    
    @Column(name = "contenido_json", nullable = false, columnDefinition = "TEXT")
    private String contenidoJson;

    @Column(length = 100)
    private String autor;
}