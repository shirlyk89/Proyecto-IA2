package com.proyectospring.gestionbodega.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Prueba de integración obligatoria: confirma que la capa de seguridad
 * bloquea con 403 a un usuario AGENTE que intenta cambiar el estado
 * de una orden (acción reservada solo para ADMIN).
 */
@SpringBootTest
@AutoConfigureMockMvc
class OrdenCompraSeguridadIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void agenteIntentaAprobarOrden_debeResponder403() throws Exception {
        String username = "agente_test_" + System.currentTimeMillis();

        // 1. Registrar un usuario de prueba con rol AGENTE
        String registroJson = """
                {"username": "%s", "password": "Test1234", "rol": "AGENTE"}
                """.formatted(username);

        MvcResult registroResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registroJson))
                .andReturn();

        System.out.println("=== REGISTRO status: " + registroResult.getResponse().getStatus());
        System.out.println("=== REGISTRO body: " + registroResult.getResponse().getContentAsString());

        // 2. Iniciar sesión para obtener un JWT real con ese rol
        String loginJson = """
                {"username": "%s", "password": "Test1234"}
                """.formatted(username);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andReturn();

        System.out.println("=== LOGIN status: " + loginResult.getResponse().getStatus());
        System.out.println("=== LOGIN body: " + loginResult.getResponse().getContentAsString());

        String loginResponseJson = loginResult.getResponse().getContentAsString();

        String token = extraerToken(loginResponseJson);

        // 3. Intentar aprobar una orden con ese token (acción exclusiva de ADMIN)
        mockMvc.perform(patch("/api/ordenes/1/estado")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\": \"APROBADA\"}"))
                // 4. Debe ser rechazado con 403, sin importar si la orden existe o no
                .andExpect(status().isForbidden());
    }

    /** Extrae el valor de "token" de un JSON simple {"token":"...","rol":"..."} sin depender de un ObjectMapper. */
    private String extraerToken(String json) {
        Matcher matcher = Pattern.compile("\"token\"\\s*:\\s*\"([^\"]+)\"").matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalStateException("No se encontró el token en la respuesta de login: " + json);
    }
}