package com.proyectospring.gestionbodega.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyectospring.gestionbodega.entities.ResumenPanel;
import com.proyectospring.gestionbodega.repositories.BodegaRepository;
import com.proyectospring.gestionbodega.repositories.OrdenCompraRepository;
import com.proyectospring.gestionbodega.repositories.ProductoRepository;
import com.proyectospring.gestionbodega.repositories.ResumenPanelRepository;
import com.proyectospring.gestionbodega.security.dtos.AccionSugeridaDto;
import com.proyectospring.gestionbodega.security.dtos.AlertaDto;
import com.proyectospring.gestionbodega.security.dtos.ResumenPanelRequest;

@Service
public class ResumenPanelServiceImpl implements ResumenPanelService {

    // ObjectMapper propio, sin depender de un bean inyectado por Spring
    // (evita el problema de renombrado de módulos en Spring Boot 4).
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ResumenPanelRepository resumenPanelRepository;
    private final ProductoRepository productoRepository;
    private final OrdenCompraRepository ordenCompraRepository;
    private final BodegaRepository bodegaRepository;

    public ResumenPanelServiceImpl(ResumenPanelRepository resumenPanelRepository,
                                    ProductoRepository productoRepository,
                                    OrdenCompraRepository ordenCompraRepository,
                                    BodegaRepository bodegaRepository) {
        this.resumenPanelRepository = resumenPanelRepository;
        this.productoRepository = productoRepository;
        this.ordenCompraRepository = ordenCompraRepository;
        this.bodegaRepository = bodegaRepository;
    }

    @Override
    @Transactional
    public ResumenPanel publicar(ResumenPanelRequest request, String autor) {
        validar(request);

        String contenidoJson = serializar(request);

        // Si ya existe un resumen para esa fecha, se reemplaza (no se crea uno nuevo)
        ResumenPanel resumen = resumenPanelRepository.findByFecha(request.getFecha())
                .orElseGet(ResumenPanel::new);

        resumen.setFecha(request.getFecha());
        resumen.setContenidoJson(contenidoJson);
        resumen.setAutor(autor);

        return resumenPanelRepository.save(resumen);
    }

    @Override
    public ResumenPanel obtenerUltimo() {
        return resumenPanelRepository.findTopByOrderByFechaDesc()
                .orElseThrow(() -> new com.proyectospring.gestionbodega.exceptions.RecursoNoEncontradoException(
                        "No hay ningún resumen publicado todavía"));
    }

    /** Convierte el request a un Map plano (sin tipos java.time) para serializar sin módulos extra. */
    private String serializar(ResumenPanelRequest request) {
        Map<String, Object> raiz = new LinkedHashMap<>();
        raiz.put("fecha", request.getFecha().toString()); // LocalDate -> "yyyy-MM-dd"
        raiz.put("narrativa", request.getNarrativa());

        List<Map<String, Object>> alertas = new ArrayList<>();
        for (AlertaDto a : request.getAlertas()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("severidad", a.getSeveridad() != null ? a.getSeveridad().name() : null);
            m.put("titulo", a.getTitulo());
            m.put("detalle", a.getDetalle());
            m.put("productoId", a.getProductoId());
            m.put("ordenId", a.getOrdenId());
            m.put("bodegaId", a.getBodegaId());
            alertas.add(m);
        }
        raiz.put("alertas", alertas);

        List<Map<String, Object>> acciones = new ArrayList<>();
        for (AccionSugeridaDto ac : request.getAccionesSugeridas()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("tipo", ac.getTipo() != null ? ac.getTipo().name() : null);
            m.put("descripcion", ac.getDescripcion());
            m.put("ordenId", ac.getOrdenId());
            m.put("productoId", ac.getProductoId());
            m.put("bodegaId", ac.getBodegaId());
            acciones.add(m);
        }
        raiz.put("accionesSugeridas", acciones);

        try {
            return objectMapper.writeValueAsString(raiz);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("No se pudo serializar el resumen del panel", e);
        }
    }

    private void validar(ResumenPanelRequest request) {
        if (request.getFecha() == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        if (request.getNarrativa() == null
                || request.getNarrativa().length() < 20
                || request.getNarrativa().length() > 500) {
            throw new IllegalArgumentException("La narrativa debe tener entre 20 y 500 caracteres");
        }
        if (request.getAlertas() == null || request.getAccionesSugeridas() == null) {
            throw new IllegalArgumentException(
                    "alertas y accionesSugeridas son obligatorios (pueden ser arreglos vacíos)");
        }

        for (AlertaDto alerta : request.getAlertas()) {
            if (alerta.getSeveridad() == null || alerta.getTitulo() == null || alerta.getDetalle() == null) {
                throw new IllegalArgumentException("Cada alerta debe incluir severidad, titulo y detalle");
            }
            long idsPresentes = contarNoNulos(alerta.getProductoId(), alerta.getOrdenId(), alerta.getBodegaId());
            if (idsPresentes < 1) {
                throw new IllegalArgumentException("Cada alerta debe enlazar al menos un identificador");
            }
            validarExistencia(alerta.getProductoId(), alerta.getOrdenId(), alerta.getBodegaId());
        }

        for (AccionSugeridaDto accion : request.getAccionesSugeridas()) {
            if (accion.getTipo() == null || accion.getDescripcion() == null) {
                throw new IllegalArgumentException("Cada acción sugerida debe incluir tipo y descripcion");
            }
            long idsPresentes = contarNoNulos(accion.getProductoId(), accion.getOrdenId(), accion.getBodegaId());
            if (idsPresentes != 1) {
                throw new IllegalArgumentException("Cada acción sugerida debe enlazar exactamente un identificador");
            }
            validarExistencia(accion.getProductoId(), accion.getOrdenId(), accion.getBodegaId());
        }
    }

    private long contarNoNulos(Object... valores) {
        return Arrays.stream(valores).filter(Objects::nonNull).count();
    }

    private void validarExistencia(Long productoId, Long ordenId, Long bodegaId) {
        if (productoId != null && !productoRepository.existsById(productoId)) {
            throw new IllegalArgumentException("No existe un producto con el ID: " + productoId);
        }
        if (ordenId != null && !ordenCompraRepository.existsById(ordenId)) {
            throw new IllegalArgumentException("No existe una orden con el ID: " + ordenId);
        }
        if (bodegaId != null && !bodegaRepository.existsById(bodegaId)) {
            throw new IllegalArgumentException("No existe una bodega con el ID: " + bodegaId);
        }
    }
}