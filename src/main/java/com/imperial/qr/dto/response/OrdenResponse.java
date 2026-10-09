package com.imperial.qr.dto.response;

import com.imperial.qr.domain.enums.EstadoOrden;
import com.imperial.qr.domain.enums.TipoOrden;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrdenResponse(
    Long id,
    TipoOrden tipo,
    Integer mesa,
    Long mesaId,
    EstadoOrden estado,
    BigDecimal subtotal,
    BigDecimal impuesto,
    BigDecimal propina,
    BigDecimal costoEnvio,
    BigDecimal total,
    BigDecimal totalPagado,
    BigDecimal saldoPendiente,
    LocalDateTime apertura,
    LocalDateTime cierre,
    List<DetalleOrdenResponse> detalles,
    DomicilioResponse domicilio,
    EncuestaResponse encuesta
) {}
