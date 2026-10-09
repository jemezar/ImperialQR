package com.imperial.qr.service;

import com.imperial.qr.domain.enums.EstadoDetalle;
import com.imperial.qr.dto.response.DetalleOrdenResponse;

import java.util.List;

public interface CocinaService {
    List<DetalleOrdenResponse> listarPendientes();
    DetalleOrdenResponse cambiarEstado(Long detalleId, EstadoDetalle nuevoEstado, Long cocineroId);
}
