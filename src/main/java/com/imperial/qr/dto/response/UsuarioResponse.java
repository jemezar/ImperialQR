package com.imperial.qr.dto.response;

import com.imperial.qr.domain.enums.Rol;
import java.time.LocalDateTime;

public record UsuarioResponse(
    Long id,
    String nombre,
    String email,
    Rol rol,
    boolean activo,
    LocalDateTime creadoEn
) {}
