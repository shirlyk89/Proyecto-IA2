package com.proyectospring.gestionbodega.security.dtos;

import com.proyectospring.gestionbodega.entities.Bodega;
import com.proyectospring.gestionbodega.entities.Producto;
import com.proyectospring.gestionbodega.entities.TipoMovimiento;
import com.proyectospring.gestionbodega.entities.Usuario;

public class MovimientoDto {
    private Producto producto;
    private Bodega bodegaOrigen;
    private Bodega bodegaDestino;
    private Usuario usuario;
    private TipoMovimiento tipo;
    private Integer cantidad;

    // Getters y Setters para cada uno de los campos
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Bodega getBodegaOrigen() { return bodegaOrigen; }
    public void setBodegaOrigen(Bodega bodegaOrigen) { this.bodegaOrigen = bodegaOrigen; }

    public Bodega getBodegaDestino() { return bodegaDestino; }
    public void setBodegaDestino(Bodega bodegaDestino) { this.bodegaDestino = bodegaDestino; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public TipoMovimiento getTipo() { return tipo; }
    public void setTipo(TipoMovimiento tipo) { this.tipo = tipo; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

}
