package com.proyectospring.gestionbodega.security.dtos;
import com.proyectospring.gestionbodega.security.Rol; 

public class RegisterRequest {
    private String username;
    private String password;
    private Rol rol;

    // Getters y Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
}