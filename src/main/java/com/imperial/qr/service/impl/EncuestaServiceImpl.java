package com.imperial.qr.service.impl;

import com.imperial.qr.domain.enums.EstadoOrden;
import com.imperial.qr.domain.model.Encuesta;
import com.imperial.qr.domain.model.Orden;
import com.imperial.qr.dto.request.RegistrarEncuestaRequest;
import com.imperial.qr.dto.response.EncuestaResponse;
import com.imperial.qr.dto.response.ResumenEncuestasResponse;
import com.imperial.qr.exception.ConflictoNegocioException;
import com.imperial.qr.exception.RecursoNoEncontradoException;
import com.imperial.qr.exception.ReglaNegocioException;
import com.imperial.qr.mapper.OrdenMapper;
import com.imperial.qr.repository.EncuestaRepository;
import com.imperial.qr.repository.OrdenRepository;
import com.imperial.qr.service.EncuestaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EncuestaServiceImpl implements EncuestaService {

    private final OrdenRepository ordenRepository;
    private final EncuestaRepository encuestaRepository;
    private final OrdenMapper mapper;

    public EncuestaServiceImpl(OrdenRepository ordenRepository,
                               EncuestaRepository encuestaRepository,
                               OrdenMapper mapper) {
        this.ordenRepository = ordenRepository;
        this.encuestaRepository = encuestaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public EncuestaResponse registrarEncuesta(Long ordenId, RegistrarEncuestaRequest request) {
        Orden orden = ordenRepository.findById(ordenId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada con ID: " + ordenId));

        // RN-11: La encuesta solo puede responderse una vez por orden y únicamente cuando la orden está PAGADA
        if (orden.getEstado() != EstadoOrden.PAGADA) {
            throw new ReglaNegocioException("La encuesta solo puede responderse cuando la orden se encuentra PAGADA (RN-11)");
        }

        if (encuestaRepository.existsByOrdenId(ordenId)) {
            throw new ConflictoNegocioException("La encuesta para esta orden ya ha sido registrada previamente (RN-11)");
        }

        Encuesta encuesta = new Encuesta(
            orden,
            request.calComida(),
            request.calServicio(),
            request.calGeneral(),
            request.comentario()
        );

        Encuesta guardada = encuestaRepository.save(encuesta);
        return mapper.toEncuestaResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenEncuestasResponse obtenerResumen() {
        Object[] raw = encuestaRepository.obtenerPromedios();
        Double avgComida = 0.0;
        Double avgServicio = 0.0;
        Double avgGeneral = 0.0;
        long total = 0;

        if (raw != null && raw.length > 0 && raw[0] instanceof Object[] arr) {
            if (arr[0] != null) avgComida = ((Number) arr[0]).doubleValue();
            if (arr[1] != null) avgServicio = ((Number) arr[1]).doubleValue();
            if (arr[2] != null) avgGeneral = ((Number) arr[2]).doubleValue();
            if (arr[3] != null) total = ((Number) arr[3]).longValue();
        }

        List<EncuestaResponse> comentarios = encuestaRepository.findAll().stream()
            .map(mapper::toEncuestaResponse)
            .toList();

        return new ResumenEncuestasResponse(avgComida, avgServicio, avgGeneral, total, comentarios);
    }
}
