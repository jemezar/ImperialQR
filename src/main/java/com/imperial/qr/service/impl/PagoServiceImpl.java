package com.imperial.qr.service.impl;

import com.imperial.qr.domain.enums.EstadoOrden;
import com.imperial.qr.domain.enums.EstadoPago;
import com.imperial.qr.domain.enums.MetodoPago;
import com.imperial.qr.domain.model.Orden;
import com.imperial.qr.domain.model.Pago;
import com.imperial.qr.dto.request.RegistrarPagoRequest;
import com.imperial.qr.dto.response.PagoResponse;
import com.imperial.qr.exception.RecursoNoEncontradoException;
import com.imperial.qr.exception.ReglaNegocioException;
import com.imperial.qr.mapper.OrdenMapper;
import com.imperial.qr.repository.MesaRepository;
import com.imperial.qr.repository.OrdenRepository;
import com.imperial.qr.repository.PagoRepository;
import com.imperial.qr.service.PagoService;
import com.imperial.qr.service.strategy.MedioPagoStrategy;
import com.imperial.qr.service.strategy.PagoResultado;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PagoServiceImpl implements PagoService {

    private final OrdenRepository ordenRepository;
    private final PagoRepository pagoRepository;
    private final MesaRepository mesaRepository;
    private final Map<MetodoPago, MedioPagoStrategy> estrategias;
    private final OrdenMapper mapper;

    public PagoServiceImpl(OrdenRepository ordenRepository,
                           PagoRepository pagoRepository,
                           MesaRepository mesaRepository,
                           List<MedioPagoStrategy> estrategiaList,
                           OrdenMapper mapper) {
        this.ordenRepository = ordenRepository;
        this.pagoRepository = pagoRepository;
        this.mesaRepository = mesaRepository;
        this.estrategias = estrategiaList.stream()
            .collect(Collectors.toMap(MedioPagoStrategy::metodo, s -> s));
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public PagoResponse registrarPago(Long ordenId, RegistrarPagoRequest request) {
        Orden orden = ordenRepository.findById(ordenId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada con ID: " + ordenId));

        if (orden.getEstado() == EstadoOrden.ABIERTA) {
            throw new ReglaNegocioException("La cuenta debe ser cerrada antes de registrar pagos");
        }
        if (orden.getEstado() == EstadoOrden.PAGADA) {
            throw new ReglaNegocioException("La orden ya se encuentra totalmente pagada");
        }

        MedioPagoStrategy estrategia = estrategias.get(request.metodo());
        if (estrategia == null) {
            throw new ReglaNegocioException("Método de pago no soportado: " + request.metodo());
        }

        PagoResultado resultado = estrategia.procesar(orden, request.monto(), request.referencia());
        EstadoPago estadoPago = resultado.exitoso() ? EstadoPago.APROBADO : EstadoPago.RECHAZADO;

        Pago pago = new Pago(orden, request.metodo(), request.monto(), resultado.referencia(), estadoPago);
        Pago pagoGuardado = pagoRepository.save(pago);
        orden.getPagos().add(pagoGuardado);

        // RN-10: La orden pasa a PAGADA cuando la suma de los pagos aprobados >= total
        BigDecimal totalPagado = orden.totalPagadoAprobado();
        if (totalPagado.compareTo(orden.getTotal()) >= 0) {
            orden.setEstado(EstadoOrden.PAGADA);
            if (orden.getMesa() != null) {
                orden.getMesa().setEstado("DISPONIBLE");
                mesaRepository.save(orden.getMesa());
            }
            ordenRepository.save(orden);
        }

        return mapper.toPagoResponse(pagoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoResponse> listarPagosPorOrden(Long ordenId) {
        return pagoRepository.findByOrdenId(ordenId).stream()
            .map(mapper::toPagoResponse)
            .toList();
    }
}
