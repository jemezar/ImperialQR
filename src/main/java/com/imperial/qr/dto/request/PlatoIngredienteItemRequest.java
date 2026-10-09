package com.imperial.qr.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record PlatoIngredienteItemRequest(
    @NotNull(message = "El id de ingrediente es requerido")
    Long ingredienteId,

    BigDecimal cantidad,
    Boolean removible,
    Boolean adicionable
) {}
