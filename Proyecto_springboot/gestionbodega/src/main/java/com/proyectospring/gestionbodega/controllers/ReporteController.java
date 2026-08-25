package com.proyectospring.gestionbodega.controllers;

import com.proyectospring.gestionbodega.services.ReporteService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/auditoria/txt")
    public ResponseEntity<byte[]> descargarReporteAuditoriaTxt() {
        byte[] contenido = reporteService.generarReporteAuditoriaTxt();

        HttpHeaders headers = new HttpHeaders();
        // Definimos las cabeceras para forzar la descarga del archivo .txt
        headers.setContentDispositionFormData("attachment", "reporte_auditoria.txt");
        headers.setContentType(MediaType.TEXT_PLAIN);

        return ResponseEntity.ok()
                .headers(headers)
                .body(contenido);
    }
}
