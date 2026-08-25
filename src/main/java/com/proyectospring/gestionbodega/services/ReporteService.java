package com.proyectospring.gestionbodega.services;

import com.proyectospring.gestionbodega.entities.Auditoria;
import com.proyectospring.gestionbodega.repositories.AuditoriaRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReporteService {

    private final AuditoriaRepository auditoriaRepository;

    public ReporteService(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    public byte[] generarReporteAuditoriaTxt() {
        List<Auditoria> auditorias = auditoriaRepository.findAll();
        StringBuilder sb = new StringBuilder();

        // Encabezado del reporte
        sb.append("=====================================================\n");
        sb.append("         REPORTE GLOBAL DE AUDITORÍA Y CONTROL       \n");
        sb.append("=====================================================\n\n");
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (auditorias.isEmpty()) {
            sb.append("No se registraron movimientos en la base de datos.\n");
        } else {
            for (Auditoria a : auditorias) {
                sb.append(String.format("[%s] | USUARIO: %-12s | OPERACIÓN: %-6s | ENTIDAD: %-10s | ID: %s\n",
                        a.getFecha().format(formatter),
                        a.getUsuario(),
                        a.getOperacion(),
                        a.getEntidadAfectada(),
                        a.getIdEntidad()
                ));
            }
        }

        sb.append("\n=====================================================\n");
        sb.append("Fin del reporte. Total de registros: ").append(auditorias.size()).append("\n");

        // Convertimos la cadena de texto a un arreglo de bytes UTF-8 para la descarga
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}