package com.imperial.qr.service;

import com.imperial.qr.dto.request.MesaRequest;
import com.imperial.qr.dto.response.MesaResponse;

import java.util.List;

public interface MesaService {
    MesaResponse obtenerPorCodigoQr(String codigoQr);
    List<MesaResponse> listarTodas();
    MesaResponse crearMesa(MesaRequest request);
    MesaResponse regenerarQr(Long mesaId);
    void eliminarMesa(Long mesaId);
}
