package com.imperial.qr.dto.response;

import com.imperial.qr.domain.enums.EstadoDetalle;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record DetalleOrdenResponse(
    Long id,
    Long platoId,
    String plato,
    Integer cantidad,
    BigDecimal precioUnitario,
    BigDecimal subtotal,
    EstadoDetalle estado,
    String observaciones,
    List<String> modificaciones,
    Integer mesaNumero,
    LocalDateTime creadoEn,
    LocalDateTime enPreparacionEn,
    LocalDateTime listoEn,
    LocalDateTime entregadoEn
) {}
