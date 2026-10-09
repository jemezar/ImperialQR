package com.imperial.qr.dto.request;

import com.imperial.qr.domain.enums.EstadoDomicilio;

public record ActualizarDomicilioRequest(
    Long domiciliarioId,
    EstadoDomicilio estado
) {}
