package com.imperial.qr.dto.request;

import com.imperial.qr.domain.enums.AccionModificacion;
import jakarta.validation.constraints.NotNull;

public record ModificacionItemRequest(
    @NotNull(message = "El id de ingrediente es requerido")
    Long ingredienteId,

    @NotNull(message = "La acción (QUITAR o AGREGAR) es requerida")
    AccionModificacion accion
) {}
