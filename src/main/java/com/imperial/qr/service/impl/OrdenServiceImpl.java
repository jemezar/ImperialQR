package com.imperial.qr.service.impl;

import com.imperial.qr.config.ImperialProperties;
import com.imperial.qr.domain.enums.AccionModificacion;
import com.imperial.qr.domain.enums.EstadoOrden;
import com.imperial.qr.domain.enums.TipoOrden;
import com.imperial.qr.domain.model.*;
import com.imperial.qr.dto.request.CrearOrdenRequest;
import com.imperial.qr.dto.request.ItemOrdenRequest;
import com.imperial.qr.dto.response.OrdenResponse;
import com.imperial.qr.exception.RecursoNoEncontradoException;
import com.imperial.qr.exception.ReglaNegocioException;
import com.imperial.qr.mapper.OrdenMapper;
import com.imperial.qr.repository.IngredienteRepository;
import com.imperial.qr.repository.MesaRepository;
import com.imperial.qr.repository.OrdenRepository;
import com.imperial.qr.repository.PlatoRepository;
import com.imperial.qr.service.OrdenService;
import com.imperial.qr.service.calculo.CalculadoraPrecio;
import com.imperial.qr.service.calculo.CalculadoraTotales;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrdenServiceImpl implements OrdenService {

    private final MesaRepository mesaRepository;
    private final OrdenRepository ordenRepository;
    private final PlatoRepository platoRepository;
    private final IngredienteRepository ingredienteRepository;
    private final CalculadoraPrecio calculadoraPrecio;
    private final CalculadoraTotales calculadoraTotales;
    private final ImperialProperties properties;
    private final OrdenMapper mapper;

    public OrdenServiceImpl(MesaRepository mesaRepository,
                            OrdenRepository ordenRepository,
                            PlatoRepository platoRepository,
                            IngredienteRepository ingredienteRepository,
                            CalculadoraPrecio calculadoraPrecio,
                            CalculadoraTotales calculadoraTotales,
                            ImperialProperties properties,
                            OrdenMapper mapper) {
        this.mesaRepository = mesaRepository;
        this.ordenRepository = ordenRepository;
        this.platoRepository = platoRepository;
        this.ingredienteRepository = ingredienteRepository;
        this.calculadoraPrecio = calculadoraPrecio;
        this.calculadoraTotales = calculadoraTotales;
        this.properties = properties;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public OrdenResponse crear(String codigoQr, CrearOrdenRequest request) {
        Mesa mesa = mesaRepository.findByCodigoQr(codigoQr)
            .orElseThrow(() -> new RecursoNoEncontradoException("Mesa no encontrada con código QR: " + codigoQr));

        return procesarOrdenMesa(mesa, request);
    }

    @Override
    @Transactional
    public OrdenResponse crearPorMesaId(Long mesaId, CrearOrdenRequest request) {
        Mesa mesa = mesaRepository.findById(mesaId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Mesa no encontrada con ID: " + mesaId));

        return procesarOrdenMesa(mesa, request);
    }

    private OrdenResponse procesarOrdenMesa(Mesa mesa, CrearOrdenRequest request) {
        // RN-01: Una mesa solo puede tener una orden en estado ABIERTA a la vez;
        // los nuevos pedidos se agregan a la misma orden.
        Orden orden = ordenRepository.findByMesaAndEstado(mesa, EstadoOrden.ABIERTA)
            .orElseGet(() -> {
                mesa.setEstado("OCUPADA");
                mesaRepository.save(mesa);
                return Orden.nuevaDeMesa(mesa);
            });

        // RN-07: No se pueden adicionar platos a una orden en estado CERRADA o PAGADA.
        if (orden.getId() != null && orden.getEstado() != EstadoOrden.ABIERTA) {
            throw new ReglaNegocioException("No se pueden adicionar platos a una orden en estado " + orden.getEstado() + " (RN-07)");
        }

        if (request.items() == null || request.items().isEmpty()) {
            throw new ReglaNegocioException("La orden debe contener al menos un plato");
        }

        for (ItemOrdenRequest item : request.items()) {
            orden.agregarDetalle(construirDetalle(item));
        }

        Orden ordenGuardada = ordenRepository.save(orden);
        return mapper.toResponse(ordenGuardada);
    }

    private DetalleOrden construirDetalle(ItemOrdenRequest item) {
        Plato plato = platoRepository.findById(item.platoId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Plato no encontrado con ID: " + item.platoId()));

        // RN-04: No se puede pedir un plato no disponible
        plato.validarDisponible();

        List<ModificacionDetalle> mods = new ArrayList<>();
        if (item.modificaciones() != null) {
            for (var modReq : item.modificaciones()) {
                Ingrediente ing = ingredienteRepository.findById(modReq.ingredienteId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Ingrediente no encontrado con ID: " + modReq.ingredienteId()));

                // RN-02 y RN-04 aplicados por el modelo de dominio
                ModificacionDetalle mod = plato.validarYCrearModificacion(ing, modReq.accion());
                mods.add(mod);
            }
        }

        // RN-03: Calculo de precio unitario
        BigDecimal precioUnitario = calculadoraPrecio.calcular(plato, mods);

        return new DetalleOrden(plato, item.cantidad(), precioUnitario, item.observaciones(), mods);
    }

    @Override
    @Transactional(readOnly = true)
    public OrdenResponse obtenerPorId(Long id) {
        Orden orden = ordenRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada con ID: " + id));
        return mapper.toResponse(orden);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenResponse> listarConFiltros(String estadoStr, Long mesaId, String tipoStr) {
        EstadoOrden estado = null;
        if (estadoStr != null && !estadoStr.isBlank()) {
            try {
                estado = EstadoOrden.valueOf(estadoStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                // ignorar o lanzar
            }
        }

        TipoOrden tipo = null;
        if (tipoStr != null && !tipoStr.isBlank()) {
            try {
                tipo = TipoOrden.valueOf(tipoStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                // ignorar
            }
        }

        return ordenRepository.buscarConFiltros(estado, mesaId, tipo).stream()
            .map(mapper::toResponse)
            .toList();
    }

    @Override
    @Transactional
    public OrdenResponse cerrar(Long ordenId, boolean incluirPropina) {
        Orden orden = ordenRepository.findById(ordenId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada con ID: " + ordenId));

        // RN-08: El administrador solo puede cerrar una orden si todos sus platos estan ENTREGADO o CANCELADO
        orden.validarPuedeCerrarse();

        orden.recalcularSubtotal();
        BigDecimal costoEnvio = orden.getTipo() == TipoOrden.DOMICILIO ? orden.getCostoEnvio() : BigDecimal.ZERO;

        // RN-09: Calculo de liquidación de totales
        CalculadoraTotales.LiquidacionCuenta liq = calculadoraTotales.liquidar(orden.getSubtotal(), incluirPropina, costoEnvio);
        orden.cerrar(liq.subtotal(), liq.impuesto(), liq.propina(), liq.costoEnvio(), liq.total());

        Orden ordenGuardada = ordenRepository.save(orden);
        return mapper.toResponse(ordenGuardada);
    }
}
