package com.imperial.qr.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record PlatoResponse(
    Long id,
    Long categoriaId,
    String categoriaNombre,
    String nombre,
    String descripcion,
    BigDecimal precioBase,
    String imagenUrl,
    boolean disponible,
    List<IngredienteResponse> receta
) {}
