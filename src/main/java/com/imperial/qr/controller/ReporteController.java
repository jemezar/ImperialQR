package com.imperial.qr.controller;

import com.imperial.qr.dto.response.ReporteVentasResponse;
import com.imperial.qr.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reportes")
@Tag(name = "Reportes Administrativos", description = "Reportes consolidados de ventas y métricas")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/ventas")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reporte de ventas del día", description = "RF-23: Retorna ventas totales, subtotales, propinas, impuestos y platos más vendidos")
    public ResponseEntity<ReporteVentasResponse> ventas(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        return ResponseEntity.ok(reporteService.generarReporteVentas(fecha));
    }
}
