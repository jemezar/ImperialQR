package com.imperial.qr.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record CrearOrdenRequest(
    String codigoQr,

    Long mesaId,

    @NotEmpty(message = "La orden debe contener al menos un plato")
    @Valid
    List<ItemOrdenRequest> items
) {}
