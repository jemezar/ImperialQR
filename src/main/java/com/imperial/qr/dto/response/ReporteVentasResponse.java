package com.imperial.qr.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record ReporteVentasResponse(
    String fecha,
    BigDecimal subtotalTotal,
    BigDecimal impuestoTotal,
    BigDecimal propinaTotal,
    BigDecimal ventasTotales,
    int cantidadOrdenesCerradas,
    List<Map<String, Object>> platosMasVendidos
) {}
