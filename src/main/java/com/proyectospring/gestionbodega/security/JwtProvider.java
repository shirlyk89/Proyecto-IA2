package com.proyectospring.gestionbodega.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    // Clave secreta para firmar el token (debe tener al menos 256 bits / 32 caracteres)
    private static final String SECRET_KEY = "clave_secreta_super_segura_para_el_proyecto_gestion_bodega_2026";
    
    // Tiempo de expiración del token: 24 horas en milisegundos
    private static final long EXPIRATION_TIME = 86400000;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // 1. Generar el token a partir de la autenticación del usuario
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date ahora = new Date();
        Date fechaExpiracion = new Date(ahora.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(ahora)
                .setExpiration(fechaExpiracion)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 2. Obtener el nombre de usuario desde el token
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // 3. Validar si el token es correcto y no ha expirado
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // El token es inválido, expiró o está mal formado
            return false;
        }
    }
}
