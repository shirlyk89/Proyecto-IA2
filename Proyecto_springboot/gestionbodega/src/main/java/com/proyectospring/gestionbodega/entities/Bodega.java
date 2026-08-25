package com.proyectospring.gestionbodega.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proyectospring.gestionbodega.security.listeners.AuditoriaListener;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@EntityListeners(AuditoriaListener.class)
@Table(name = "bodega", schema = "logitrack")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Bodega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 225)
    private String ubicacion;

    @Column(nullable = false)
    private Integer capacidad;

    @Column(nullable = false, length = 225)
    private String encargado;

    // Relación OneToMany para los movimientos donde esta bodega fue ORIGEN
    @OneToMany(mappedBy = "bodegaOrigen", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Movimiento> movimientosOrigen;

    // Relación OneToMany para los movimientos donde esta bodega fue DESTINO
    @OneToMany(mappedBy = "bodegaDestino", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Movimiento> movimientosDestino;
}