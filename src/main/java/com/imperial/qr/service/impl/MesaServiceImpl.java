package com.imperial.qr.service.impl;

import com.imperial.qr.domain.enums.EstadoOrden;
import com.imperial.qr.domain.model.Mesa;
import com.imperial.qr.domain.model.Orden;
import com.imperial.qr.dto.request.MesaRequest;
import com.imperial.qr.dto.response.MesaResponse;
import com.imperial.qr.dto.response.OrdenResponse;
import com.imperial.qr.exception.ConflictoNegocioException;
import com.imperial.qr.exception.RecursoNoEncontradoException;
import com.imperial.qr.mapper.OrdenMapper;
import com.imperial.qr.repository.MesaRepository;
import com.imperial.qr.repository.OrdenRepository;
import com.imperial.qr.service.MesaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MesaServiceImpl implements MesaService {

    private final MesaRepository mesaRepository;
    private final OrdenRepository ordenRepository;
    private final OrdenMapper mapper;

    public MesaServiceImpl(MesaRepository mesaRepository,
                           OrdenRepository ordenRepository,
                           OrdenMapper mapper) {
        this.mesaRepository = mesaRepository;
        this.ordenRepository = ordenRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public MesaResponse obtenerPorCodigoQr(String codigoQr) {
        Mesa mesa = mesaRepository.findByCodigoQr(codigoQr)
            .orElseThrow(() -> new RecursoNoEncontradoException("Mesa no encontrada con código QR: " + codigoQr));

        Optional<Orden> ordenOpt = ordenRepository.findByMesaAndEstado(mesa, EstadoOrden.ABIERTA);
        OrdenResponse ordenResponse = ordenOpt.map(mapper::toResponse).orElse(null);

        return new MesaResponse(
            mesa.getId(),
            mesa.getNumero(),
            mesa.getCapacidad(),
            mesa.getCodigoQr(),
            mesa.getEstado(),
            ordenResponse
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<MesaResponse> listarTodas() {
        return mesaRepository.findAll().stream()
            .map(m -> {
                Optional<Orden> ordenOpt = ordenRepository.findByMesaAndEstado(m, EstadoOrden.ABIERTA);
                OrdenResponse ordenResponse = ordenOpt.map(mapper::toResponse).orElse(null);
                return new MesaResponse(
                    m.getId(),
                    m.getNumero(),
                    m.getCapacidad(),
                    m.getCodigoQr(),
                    m.getEstado(),
                    ordenResponse
                );
            })
            .toList();
    }

    @Override
    @Transactional
    public MesaResponse crearMesa(MesaRequest request) {
        if (mesaRepository.existsByNumero(request.numero())) {
            throw new ConflictoNegocioException("Ya existe una mesa con el número: " + request.numero());
        }

        Mesa mesa = new Mesa(request.numero(), request.capacidad());
        Mesa guardada = mesaRepository.save(mesa);
        return MesaResponse.sinOrden(
            guardada.getId(),
            guardada.getNumero(),
            guardada.getCapacidad(),
            guardada.getCodigoQr(),
            guardada.getEstado()
        );
    }

    @Override
    @Transactional
    public MesaResponse regenerarQr(Long mesaId) {
        Mesa mesa = mesaRepository.findById(mesaId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Mesa no encontrada con ID: " + mesaId));

        mesa.regenerarQr();
        Mesa guardada = mesaRepository.save(mesa);

        Optional<Orden> ordenOpt = ordenRepository.findByMesaAndEstado(guardada, EstadoOrden.ABIERTA);
        OrdenResponse ordenResponse = ordenOpt.map(mapper::toResponse).orElse(null);

        return new MesaResponse(
            guardada.getId(),
            guardada.getNumero(),
            guardada.getCapacidad(),
            guardada.getCodigoQr(),
            guardada.getEstado(),
            ordenResponse
        );
    }

    @Override
    @Transactional
    public void eliminarMesa(Long mesaId) {
        if (!mesaRepository.existsById(mesaId)) {
            throw new RecursoNoEncontradoException("Mesa no encontrada con ID: " + mesaId);
        }
        mesaRepository.deleteById(mesaId);
    }
}
