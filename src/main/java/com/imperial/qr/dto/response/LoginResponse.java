package com.imperial.qr.dto.response;

import com.imperial.qr.domain.enums.Rol;

public record LoginResponse(
    String token,
    String tipo,
    Long id,
    String nombre,
    String email,
    Rol rol
) {}
