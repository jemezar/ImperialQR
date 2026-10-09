package com.imperial.qr.service;

import com.imperial.qr.dto.response.DetalleOrdenResponse;

import java.util.List;

public interface MeseroService {
    List<DetalleOrdenResponse> listarPlatosListos();
    DetalleOrdenResponse marcarEntregado(Long detalleId, Long meseroId);
}
