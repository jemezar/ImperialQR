package com.imperial.qr.service.impl;

import com.imperial.qr.domain.enums.EstadoOrden;
import com.imperial.qr.domain.model.Orden;
import com.imperial.qr.dto.response.ReporteVentasResponse;
import com.imperial.qr.repository.DetalleOrdenRepository;
import com.imperial.qr.repository.OrdenRepository;
import com.imperial.qr.service.ReporteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReporteServiceImpl implements ReporteService {

    private final OrdenRepository ordenRepository;
    private final DetalleOrdenRepository detalleOrdenRepository;

    public ReporteServiceImpl(OrdenRepository ordenRepository, DetalleOrdenRepository detalleOrdenRepository) {
        this.ordenRepository = ordenRepository;
        this.detalleOrdenRepository = detalleOrdenRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteVentasResponse generarReporteVentas(LocalDate fecha) {
        LocalDate f = (fecha != null) ? fecha : LocalDate.now();
        LocalDateTime desde = f.atStartOfDay();
        LocalDateTime hasta = f.atTime(LocalTime.MAX);

        List<Orden> ordenes = ordenRepository.findByFechaRangoAndEstado(desde, hasta, EstadoOrden.PAGADA);

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal impuesto = BigDecimal.ZERO;
        BigDecimal propina = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;

        for (Orden o : ordenes) {
            if (o.getSubtotal() != null) subtotal = subtotal.add(o.getSubtotal());
            if (o.getImpuesto() != null) impuesto = impuesto.add(o.getImpuesto());
            if (o.getPropina() != null) propina = propina.add(o.getPropina());
            if (o.getTotal() != null) total = total.add(o.getTotal());
        }

        List<Object[]> rawPlatos = detalleOrdenRepository.platosMasVendidos(desde, hasta);
        List<Map<String, Object>> platosMasVendidos = new ArrayList<>();
        for (Object[] r : rawPlatos) {
            Map<String, Object> item = new HashMap<>();
            item.put("plato", r[0]);
            item.put("cantidad", r[1]);
            platosMasVendidos.add(item);
        }

        return new ReporteVentasResponse(
            f.toString(),
            subtotal,
            impuesto,
            propina,
            total,
            ordenes.size(),
            platosMasVendidos
        );
    }
}
