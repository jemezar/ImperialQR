package com.imperial.qr.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record PlatoRequest(
    @NotNull(message = "La categoría es requerida")
    Long categoriaId,

    @NotBlank(message = "El nombre del plato es requerido")
    String nombre,

    String descripcion,

    @NotNull(message = "El precio base es requerido")
    @DecimalMin(value = "0.01", message = "El precio base debe ser mayor a 0")
    BigDecimal precioBase,

    String imagenUrl,

    Boolean disponible,

    List<PlatoIngredienteItemRequest> receta
) {}
