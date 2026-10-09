package com.imperial.qr.service;

import com.imperial.qr.domain.enums.EstadoDomicilio;
import com.imperial.qr.dto.request.CrearDomicilioRequest;
import com.imperial.qr.dto.response.DomicilioResponse;

import java.util.List;

public interface DomicilioService {
    DomicilioResponse crearDomicilio(CrearDomicilioRequest request);
    DomicilioResponse actualizarDomicilio(Long domicilioId, Long domiciliarioId, EstadoDomicilio nuevoEstado);
    DomicilioResponse obtenerPorId(Long domicilioId);
    List<DomicilioResponse> listar(EstadoDomicilio estado);
}
