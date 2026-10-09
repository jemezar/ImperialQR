package com.imperial.qr.dto.response;

import com.imperial.qr.domain.enums.EstadoPago;
import com.imperial.qr.domain.enums.MetodoPago;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoResponse(
    Long id,
    Long ordenId,
    MetodoPago metodo,
    BigDecimal monto,
    String referencia,
    EstadoPago estado,
    LocalDateTime fecha,
    BigDecimal totalOrden,
    BigDecimal totalPagado,
    BigDecimal saldoPendiente
) {}
