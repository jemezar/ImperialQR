package com.imperial.qr.service.impl;

import com.imperial.qr.domain.enums.EstadoDetalle;
import com.imperial.qr.domain.model.DetalleOrden;
import com.imperial.qr.dto.response.DetalleOrdenResponse;
import com.imperial.qr.exception.RecursoNoEncontradoException;
import com.imperial.qr.mapper.OrdenMapper;
import com.imperial.qr.repository.DetalleOrdenRepository;
import com.imperial.qr.service.MeseroService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MeseroServiceImpl implements MeseroService {

    private final DetalleOrdenRepository detalleOrdenRepository;
    private final OrdenMapper mapper;

    public MeseroServiceImpl(DetalleOrdenRepository detalleOrdenRepository, OrdenMapper mapper) {
        this.detalleOrdenRepository = detalleOrdenRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetalleOrdenResponse> listarPlatosListos() {
        return detalleOrdenRepository.findByEstadoOrderByListoEnAsc(EstadoDetalle.LISTO).stream()
            .map(mapper::toDetalleResponse)
            .toList();
    }

    @Override
    @Transactional
    public DetalleOrdenResponse marcarEntregado(Long detalleId, Long meseroId) {
        DetalleOrden detalle = detalleOrdenRepository.findById(detalleId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Detalle de orden no encontrado con ID: " + detalleId));

        // RN-05 y RN-06: el mesero marca ENTREGADO en mesa
        detalle.cambiarEstado(EstadoDetalle.ENTREGADO, meseroId);

        DetalleOrden guardado = detalleOrdenRepository.save(detalle);
        return mapper.toDetalleResponse(guardado);
    }
}
