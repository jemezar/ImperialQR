package com.imperial.qr.service;

import com.imperial.qr.dto.request.CrearOrdenRequest;
import com.imperial.qr.dto.response.OrdenResponse;

import java.util.List;

public interface OrdenService {
    OrdenResponse crear(String codigoQr, CrearOrdenRequest request);
    OrdenResponse crearPorMesaId(Long mesaId, CrearOrdenRequest request);
    OrdenResponse obtenerPorId(Long id);
    List<OrdenResponse> listarConFiltros(String estado, Long mesaId, String tipo);
    OrdenResponse cerrar(Long ordenId, boolean incluirPropina);
}
