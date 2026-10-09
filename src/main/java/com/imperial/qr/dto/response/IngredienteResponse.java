package com.imperial.qr.dto.response;

import java.math.BigDecimal;

public record IngredienteResponse(
    Long id,
    String nombre,
    String unidad,
    BigDecimal precioExtra,
    boolean disponible,
    Boolean removible,
    Boolean adicionable,
    BigDecimal cantidad
) {
    public static IngredienteResponse simple(Long id, String nombre, String unidad, BigDecimal precioExtra, boolean disponible) {
        return new IngredienteResponse(id, nombre, unidad, precioExtra, disponible, null, null, null);
    }
}
