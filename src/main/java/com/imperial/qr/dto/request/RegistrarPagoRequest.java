package com.imperial.qr.dto.request;

import com.imperial.qr.domain.enums.MetodoPago;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record RegistrarPagoRequest(
    @NotNull(message = "El método de pago es requerido")
    MetodoPago metodo,

    @NotNull(message = "El monto es requerido")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    BigDecimal monto,

    String referencia
) {}
