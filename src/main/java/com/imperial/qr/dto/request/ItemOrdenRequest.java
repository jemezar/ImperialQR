package com.imperial.qr.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ItemOrdenRequest(
    @NotNull(message = "El platoId es requerido")
    Long platoId,

    @NotNull(message = "La cantidad es requerida")
    @Min(value = 1, message = "La cantidad mínima es 1")
    Integer cantidad,

    String observaciones,

    @Valid
    List<ModificacionItemRequest> modificaciones
) {}
