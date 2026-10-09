package com.imperial.qr.dto.response;

import java.util.List;

public record ResumenEncuestasResponse(
    Double promedioComida,
    Double promedioServicio,
    Double promedioGeneral,
    long totalEncuestas,
    List<EncuestaResponse> comentariosRecientes
) {}
