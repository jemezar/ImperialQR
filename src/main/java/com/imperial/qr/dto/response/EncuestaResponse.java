package com.imperial.qr.dto.response;

import java.time.LocalDateTime;

public record EncuestaResponse(
    Long id,
    Long ordenId,
    Integer calComida,
    Integer calServicio,
    Integer calGeneral,
    String comentario,
    LocalDateTime creadoEn
) {}
