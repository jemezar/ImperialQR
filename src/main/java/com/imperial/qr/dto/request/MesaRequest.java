package com.imperial.qr.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MesaRequest(
    @NotNull(message = "El número de mesa es requerido")
    Integer numero,

    @NotNull(message = "La capacidad es requerida")
    @Min(value = 1, message = "La capacidad mínima es 1")
    Integer capacidad
) {}
