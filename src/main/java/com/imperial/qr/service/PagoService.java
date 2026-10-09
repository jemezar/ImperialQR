package com.imperial.qr.service;

import com.imperial.qr.dto.request.RegistrarPagoRequest;
import com.imperial.qr.dto.response.PagoResponse;

import java.util.List;

public interface PagoService {
    PagoResponse registrarPago(Long ordenId, RegistrarPagoRequest request);
    List<PagoResponse> listarPagosPorOrden(Long ordenId);
}
