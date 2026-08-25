package com.proyectospring.gestionbodega.controllers;

import com.proyectospring.gestionbodega.entities.Usuario;
import com.proyectospring.gestionbodega.repositories.UsuarioRepository;
import com.proyectospring.gestionbodega.security.JwtProvider;
import com.proyectospring.gestionbodega.security.dtos.JwtResponse;
import com.proyectospring.gestionbodega.security.dtos.LoginRequest;
import com.proyectospring.gestionbodega.security.dtos.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthController(AuthenticationManager authenticationManager, UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Error: El nombre de usuario ya existe.");
        }

        Usuario usuario = new Usuario(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()), // Encriptamos la contraseña
                request.getRol()
        );

        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Usuario registrado exitosamente");
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
        // Spring Security maneja la verificación de la contraseña aquí
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);
    
    // Extraemos el rol (Authority) del usuario autenticado
    org.springframework.security.core.userdetails.UserDetails userDetails = 
        (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();
    
    String rol = userDetails.getAuthorities().iterator().next().getAuthority();
    
    // Generamos el token
    String jwt = jwtProvider.generateToken(authentication);
    
    // Devolvemos el token Y el rol al frontend
    return ResponseEntity.ok(new JwtResponse(jwt, rol));
    }
}