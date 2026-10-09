package com.imperial.qr.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record CrearDomicilioRequest(
    @NotBlank(message = "El nombre del cliente es requerido")
    String nombreCliente,

    @NotBlank(message = "El teléfono es requerido")
    String telefono,

    @NotBlank(message = "La dirección es requerida")
    String direccion,

    String notasDireccion,

    @NotNull(message = "El costo de envío es requerido")
    @DecimalMin(value = "0.0", message = "El costo de envío no puede ser negativo")
    BigDecimal costoEnvio,

    @NotEmpty(message = "El domicilio debe contener al menos un plato")
    @Valid
    List<ItemOrdenRequest> items
) {}
