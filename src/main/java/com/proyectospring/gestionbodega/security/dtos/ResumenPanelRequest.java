package com.proyectospring.gestionbodega.security.dtos;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ResumenPanelRequest {
    private LocalDate fecha;
    private String narrativa;
    private List<AlertaDto> alertas;
    private List<AccionSugeridaDto> accionesSugeridas;
}