package com.imperial.qr.service;

import com.imperial.qr.dto.response.ReporteVentasResponse;

import java.time.LocalDate;

public interface ReporteService {
    ReporteVentasResponse generarReporteVentas(LocalDate fecha);
}
