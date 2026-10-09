package com.imperial.qr.dto.response;

import java.util.List;

public record CategoriaResponse(
    Long id,
    String nombre,
    String descripcion,
    boolean activo,
    List<PlatoResponse> platos
) {}
