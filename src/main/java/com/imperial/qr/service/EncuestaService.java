package com.imperial.qr.service;

import com.imperial.qr.dto.request.RegistrarEncuestaRequest;
import com.imperial.qr.dto.response.EncuestaResponse;
import com.imperial.qr.dto.response.ResumenEncuestasResponse;

public interface EncuestaService {
    EncuestaResponse registrarEncuesta(Long ordenId, RegistrarEncuestaRequest request);
    ResumenEncuestasResponse obtenerResumen();
}
