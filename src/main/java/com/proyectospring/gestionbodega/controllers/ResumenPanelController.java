package com.proyectospring.gestionbodega.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyectospring.gestionbodega.entities.ResumenPanel;
import com.proyectospring.gestionbodega.security.dtos.ResumenPanelRequest;
import com.proyectospring.gestionbodega.services.ResumenPanelService;

@RestController
@RequestMapping("/api/panel/resumen")
public class ResumenPanelController {

    private final ResumenPanelService resumenPanelService;

    public ResumenPanelController(ResumenPanelService resumenPanelService) {
        this.resumenPanelService = resumenPanelService;
    }

    @PostMapping
    public ResumenPanel publicar(@RequestBody ResumenPanelRequest request, Authentication authentication) {
        return resumenPanelService.publicar(request, authentication.getName());
    }

    @GetMapping
    public ResumenPanel obtenerUltimo() {
        return resumenPanelService.obtenerUltimo();
    }
}
