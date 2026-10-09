package com.imperial.qr.controller;

import com.imperial.qr.dto.response.ResumenEncuestasResponse;
import com.imperial.qr.service.EncuestaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/encuestas")
@Tag(name = "Encuestas de Satisfacción", description = "Reportes y promedios de satisfacción de clientes")
public class EncuestaController {

    private final EncuestaService encuestaService;

    public EncuestaController(EncuestaService encuestaService) {
        this.encuestaService = encuestaService;
    }

    @GetMapping("/resumen")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Resumen de encuestas", description = "RF-14: Retorna promedios de calificación de comida, servicio, calificación general y comentarios")
    public ResponseEntity<ResumenEncuestasResponse> obtenerResumen() {
        return ResponseEntity.ok(encuestaService.obtenerResumen());
    }
}
