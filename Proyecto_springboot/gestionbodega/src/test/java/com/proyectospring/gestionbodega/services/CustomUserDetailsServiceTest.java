package com.proyectospring.gestionbodega.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.proyectospring.gestionbodega.entities.Usuario;
import com.proyectospring.gestionbodega.repositories.UsuarioRepository;
import com.proyectospring.gestionbodega.security.CustomUserDetailsService;
import com.proyectospring.gestionbodega.security.Rol;


@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {


    @Mock
    private UsuarioRepository usuarioRepository;


    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;


    private Usuario usuario;



    @BeforeEach
    void setUp() {

        usuario = new Usuario(
                "shirly",
                "password123",
                Rol.ADMIN
        );

        usuario.setId(1L);
    }



    @Test
    void loadUserByUsernameDebeRetornarUsuario() {


        when(usuarioRepository.findByUsername("shirly"))
                .thenReturn(Optional.of(usuario));


        UserDetails resultado =
                customUserDetailsService.loadUserByUsername("shirly");


        assertNotNull(resultado);

        assertEquals(
                "shirly",
                resultado.getUsername()
        );

        assertEquals(
                "password123",
                resultado.getPassword()
        );


        assertTrue(
                resultado.getAuthorities()
                .stream()
                .anyMatch(
                    authority ->
                    authority.getAuthority().equals("ROLE_ADMIN")
                )
        );


        verify(usuarioRepository)
                .findByUsername("shirly");
    }



    @Test
    void loadUserByUsernameUsuarioNoExisteDebeLanzarError() {


        when(usuarioRepository.findByUsername("desconocido"))
                .thenReturn(Optional.empty());


        UsernameNotFoundException exception =
                assertThrows(
                    UsernameNotFoundException.class,
                    () -> customUserDetailsService
                            .loadUserByUsername("desconocido")
                );


        assertEquals(
                "Usuario no encontrado: desconocido",
                exception.getMessage()
        );


        verify(usuarioRepository)
                .findByUsername("desconocido");
    }

}