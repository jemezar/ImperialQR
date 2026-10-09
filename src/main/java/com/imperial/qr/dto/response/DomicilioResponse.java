package com.imperial.qr.dto.response;

import com.imperial.qr.domain.enums.EstadoDomicilio;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DomicilioResponse(
    Long id,
    Long ordenId,
    String nombreCliente,
    String telefono,
    String direccion,
    String notasDireccion,
    BigDecimal costoEnvio,
    EstadoDomicilio estado,
    Long domiciliarioId,
    String domiciliarioNombre,
    LocalDateTime creadoEn
) {}
