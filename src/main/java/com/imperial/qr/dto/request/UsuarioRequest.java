package com.imperial.qr.dto.request;

import com.imperial.qr.domain.enums.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioRequest(
    @NotBlank(message = "El nombre es requerido")
    String nombre,

    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe ser válido")
    String email,

    String password,

    @NotNull(message = "El rol es requerido")
    Rol rol,

    Boolean activo
) {}
