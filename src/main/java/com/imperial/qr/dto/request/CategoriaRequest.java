package com.imperial.qr.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRequest(
    @NotBlank(message = "El nombre de la categoría es requerido")
    String nombre,

    String descripcion,

    Boolean activo
) {}
