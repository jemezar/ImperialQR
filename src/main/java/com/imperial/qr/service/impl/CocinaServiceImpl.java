package com.imperial.qr.service.impl;

import com.imperial.qr.domain.enums.EstadoDetalle;
import com.imperial.qr.domain.model.DetalleOrden;
import com.imperial.qr.dto.response.DetalleOrdenResponse;
import com.imperial.qr.exception.AccesoDenegadoException;
import com.imperial.qr.exception.RecursoNoEncontradoException;
import com.imperial.qr.exception.ReglaNegocioException;
import com.imperial.qr.mapper.OrdenMapper;
import com.imperial.qr.repository.DetalleOrdenRepository;
import com.imperial.qr.service.CocinaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CocinaServiceImpl implements CocinaService {

    private final DetalleOrdenRepository detalleOrdenRepository;
    private final OrdenMapper mapper;

    public CocinaServiceImpl(DetalleOrdenRepository detalleOrdenRepository, OrdenMapper mapper) {
        this.detalleOrdenRepository = detalleOrdenRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetalleOrdenResponse> listarPendientes() {
        // Devuelve platos en estado RECIBIDO y EN_PREPARACION ordenados por antigüedad
        List<EstadoDetalle> estados = List.of(EstadoDetalle.RECIBIDO, EstadoDetalle.EN_PREPARACION);
        return detalleOrdenRepository.findByEstadoInOrderByCreadoEnAsc(estados).stream()
            .map(mapper::toDetalleResponse)
            .toList();
    }

    @Override
    @Transactional
    public DetalleOrdenResponse cambiarEstado(Long detalleId, EstadoDetalle nuevoEstado, Long cocineroId) {
        DetalleOrden detalle = detalleOrdenRepository.findById(detalleId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Detalle de orden no encontrado con ID: " + detalleId));

        // RN-06: Solo el cocinero cambia a EN_PREPARACION y LISTO. No puede marcar ENTREGADO.
        if (nuevoEstado != EstadoDetalle.EN_PREPARACION && nuevoEstado != EstadoDetalle.LISTO) {
            throw new AccesoDenegadoException("El cocinero solo puede cambiar platos a EN_PREPARACION o LISTO (RN-06)");
        }

        // RN-05: Validar y registrar la transición
        detalle.cambiarEstado(nuevoEstado, cocineroId);

        DetalleOrden guardado = detalleOrdenRepository.save(detalle);
        return mapper.toDetalleResponse(guardado);
    }
}
