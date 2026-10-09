package com.imperial.qr.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record IngredienteRequest(
    @NotBlank(message = "El nombre del ingrediente es requerido")
    String nombre,

    String unidad,

    @NotNull(message = "El precio extra es requerido")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio extra debe ser mayor o igual a 0")
    BigDecimal precioExtra,

    Boolean disponible
) {}
