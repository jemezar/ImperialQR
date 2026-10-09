package com.imperial.qr.dto.request;

import com.imperial.qr.domain.enums.EstadoDetalle;
import jakarta.validation.constraints.NotNull;

public record CambioEstadoDetalleRequest(
    @NotNull(message = "El nuevo estado es requerido")
    EstadoDetalle nuevoEstado
) {}
